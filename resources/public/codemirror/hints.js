var project;
var Pos = CodeMirror.Pos;
var cmpPos = CodeMirror.cmpPos;
var cls = "CodeMirror-Tern-";

function elFromString(s) {
  var frag = document.createDocumentFragment(),
      temp = document.createElement('span');
  temp.innerHTML = s;
  while (temp.firstChild) {
    frag.appendChild(temp.firstChild);
  }
  return frag;
}

function elt(tagname, cls /*, ... elts*/ ) {
  var e = document.createElement(tagname);
  if (cls) e.className = cls;
  for (var i = 2; i < arguments.length; ++i) {
    var elt = arguments[i];
    if (typeof elt == "string") elt = document.createTextNode(elt);
    e.appendChild(elt);
  }
  return e;
}

function htmlEncode(string) {
  var entityMap = {
    "<": "&lt;",
    ">": "&gt;"
  };
  return String(string).replace(/[<>]/g, function (s) {
    if (!s) return '';
    return entityMap[s];
  });
}

// Tooltips

function tempTooltip(cm, content, ts) {
  if (cm.state.ternTooltip) remove(cm.state.ternTooltip);
  var where = cm.cursorCoords();
  var tip = cm.state.ternTooltip = makeTooltip(where.right + 1, where.bottom, content);
  function maybeClear() {
    old = true;
    if (!mouseOnTip) clear();
  }
  function clear() {
    cm.state.ternTooltip = null;
    if (!tip.parentNode) return;
    cm.off("cursorActivity", clear);
    cm.off('blur', clear);
    cm.off('scroll', clear);
    fadeOut(tip);
  }
  var mouseOnTip = false, old = false;
  CodeMirror.on(tip, "mousemove", function() { mouseOnTip = true; });
  CodeMirror.on(tip, "mouseout", function(e) {
    if (!CodeMirror.contains(tip, e.relatedTarget || e.toElement)) {
      if (old) clear();
      else mouseOnTip = false;
    }
  });
  setTimeout(maybeClear, ts.options.hintDelay ? ts.options.hintDelay : 1700);
  cm.on("cursorActivity", clear);
  cm.on('blur', clear);
  cm.on('scroll', clear);
}

function makeTooltip(x, y, content) {
  var node = elt("div", cls + "tooltip", content);
  node.style.left = x + "px";
  node.style.top = y + "px";
  document.body.appendChild(node);
  return node;
}

function remove(node) {
  var p = node && node.parentNode;
  if (p) p.removeChild(node);
}

function fadeOut(tooltip) {
  tooltip.style.opacity = "0";
  setTimeout(function() { remove(tooltip); }, 1100);
}

function showError(ts, cm, msg) {
  if (ts.options.showError)
    ts.options.showError(cm, msg);
  else
    tempTooltip(cm, String(msg), ts);
}

function closeArgHints(ts) {
  if (ts.activeArgHints) { remove(ts.activeArgHints); ts.activeArgHints = null; }
}

function parseJsDocParams(str) {
  if (!str) return [];
  str = str.replace(/@param/gi, '@param'); //make sure all param tags are lowercase
  var params = [];
  while (str.indexOf('@param') !== -1) {
    str = str.substring(str.indexOf('@param') + 6); //starting after first param match
    var nextTagStart = str.indexOf('@'); //split on next param (will break if @symbol inside of param, like a link... dont have to time fullproof right now)

    var paramStr = nextTagStart === -1 ? str : str.substr(0, nextTagStart);
    var thisParam = {
      name: "",
      parentName: "",
      type: "",
      description: "",
      optional: false,
      defaultValue: ""
    };
    var re = /\s{[^}]{1,50}}\s/;
    var m;
    while ((m = re.exec(paramStr)) !== null) {
      if (m.index === re.lastIndex) {
        re.lastIndex++;
      }
      thisParam.type = m[0];
      paramStr = paramStr.replace(thisParam.type, '').trim(); //remove type from param string
      thisParam.type = thisParam.type.replace('{', '').replace('}', '').replace(' ', '').trim(); //remove brackets and spaces
    }
    paramStr = paramStr.trim(); //we now have a single param string starting after the type, next string should be the parameter name
    if (paramStr.substr(0, 1) === '[') {
      thisParam.optional = true;
      var endBracketIdx = paramStr.indexOf(']');
      if (endBracketIdx === -1) {
        showError('failed to parse parameter name; Found starting \'[\' but missing closing \']\'');
        continue; //go to next
      }
      var nameStr = paramStr.substring(0, endBracketIdx + 1);
      paramStr = paramStr.replace(nameStr, '').trim(); //remove name portion from param str
      nameStr = nameStr.replace('[', '').replace(']', ''); //remove brackets
      if (nameStr.indexOf('=') !== -1) {
        var defaultValue = nameStr.substr(nameStr.indexOf('=') + 1);
        if (defaultValue.trim() === '') {
          thisParam.defaultValue = "undefined";
        }
        else {
          thisParam.defaultValue = defaultValue.trim();
        }
        thisParam.name = nameStr.substring(0, nameStr.indexOf('=')).trim(); //set name
      }
      else {
        thisParam.name = nameStr.trim();
      }
    }
    else { //not optional
      var nextSpace = paramStr.indexOf(' ');
      if (nextSpace !== -1) {
        thisParam.name = paramStr.substr(0, nextSpace);
        paramStr = paramStr.substr(nextSpace).trim(); //remove name portion from param str
      }
      else { //no more spaces left, next portion of string must be name and there is no description
        thisParam.name = paramStr;
        paramStr = '';
      }
    }
    var nameDotIdx = thisParam.name.indexOf('.');
    if (nameDotIdx !== -1) {
      thisParam.parentName = thisParam.name.substring(0, nameDotIdx);
      thisParam.name = thisParam.name.substring(nameDotIdx + 1);
    }
    paramStr = paramStr.trim();
    if (paramStr.length > 0) {
      thisParam.description = paramStr.replace('-', '').trim(); //optional hiphen specified before start of description
    }
    thisParam.name = htmlEncode(thisParam.name);
    thisParam.parentName = htmlEncode(thisParam.parentName);
    thisParam.description = htmlEncode(thisParam.description);
    thisParam.type = htmlEncode(thisParam.type);
    thisParam.defaultValue = htmlEncode(thisParam.defaultValue);
    params.push(thisParam);
  }
  return params;
}

// ---------------------------------------------------------------------------------------------------------------------

function completionTip(data, activeArg) {
  //console.log("completionTip!");
  if (!data || (!data.doc && !data.fnArgs)) return null;

  var tip = elt("span", null);
  var d = data.doc;
  var params = data.params || parseJsDocParams(d); //parse params

  var fnArgs = data.fnArgs ? data.fnArgs : data.type ? parseFnType(data.type) : null; //will be null if parseFnType detects that this is not a function

  if (fnArgs) {
    var getParam = function (arg, getChildren) {
      if (params === null) return null;
      if (!arg.name) return null;
      var children = [];
      for (var i = 0; i < params.length; i++) {
        if (getChildren === true) {
          if (params[i].parentName.toLowerCase().trim() === arg.name.toLowerCase().trim()) {
            children.push(params[i]);
          }
        }
        else {
          if (params[i].name.toLowerCase().trim() === arg.name.toLowerCase().trim()) {
            return params[i];
          }
        }
      }
      if (getChildren === true) return children;
      return null;
    };
    var getParamDetailedName = function (param) {
      var name = param.name;
      if (param.optional === true) {
        if (param.defaultValue) {
          name = "[" + name + "=" + param.defaultValue + "]";
        }
        else {
          name = "[" + name + "]";
        }
      }
      return name;
    };
    var useDetailedArgHints = params.length === 0 || !isNaN(parseInt(activeArg));
    var typeStr = '';
    typeStr += htmlEncode(data.exprName || data.name || "fn");
    typeStr += "(";
    var activeParam = null,
        activeParamChildren = []; //one ore more child params for multiple object properties

    for (var i = 0; i < fnArgs.args.length; i++) {
      var paramStr = '';
      var isCurrent = !isNaN(parseInt(activeArg)) ? i === activeArg : false;
      var arg = fnArgs.args[i]; //name,type
      var name = arg.name || "?";
      if (name.length > 1 && name.substr(name.length - 1) === '?') {
        name = name.substr(0, name.length - 1);
        arg.name = name; //update the arg var with proper name for use below
      }

      if (!useDetailedArgHints) {
        paramStr += htmlEncode(name);
      }
      else {
        var param = getParam(arg, false);
        var children = getParam(arg, true);
        var type = arg.type;
        var optional = false;
        var defaultValue = '';
        if (param !== null) {
          name = param.name;
          if (param.type) {
            type = param.type;
          }
          if (isCurrent) {
            activeParam = param;
          }
          optional = param.optional;
          defaultValue = param.defaultValue.trim();
        }

        // true naming
        if (type == 'bool') type = 'boolean';
        if (type == 'ALL') type = '*';
        if (type == 'FUNCTION') type = 'function';
        if (type == 'VAR_ARGS') type = '...*';

        if (children && children.length > 0) {
          if (isCurrent) {
            activeParamChildren = children;
          }
          type = "{";
          for (var c = 0; c < children.length; c++) {
            type += children[c].name;
            if (c + 1 !== children.length && children.length > 1) type += ",&nbsp;";
          }
          type += "}";
        }
        paramStr += type ? '<span class="' + cls + 'type">' + htmlEncode(type) + '</span>&nbsp;' : '';
        paramStr += '<span class="' + cls + (isCurrent ? "farg-current" : "farg") + '">' + (htmlEncode(name) || "?") + '</span>';
        if (defaultValue !== '') {
          paramStr += '<span class="' + cls + 'jsdoc-param-defaultValue">=' + htmlEncode(defaultValue) + '</span>';
        }
        if (optional) {
          paramStr = '<span class="' + cls + 'jsdoc-param-optionalWrapper">' + '<span class="' + cls + 'farg-optionalBracket">[</span>' + paramStr + '<span class="' + cls + 'jsdoc-param-optionalBracket">]</span>' + '</span>';
        }
      }
      if (i > 0) paramStr = ', ' + paramStr;
      typeStr += paramStr;
    }

    typeStr += ")";
    if (fnArgs.rettype) {
      if (useDetailedArgHints) {
        typeStr += ' -> <span class="' + cls + 'type">' + htmlEncode(fnArgs.rettype) + '</span>';
      }
      else {
        typeStr += ' -> ' + htmlEncode(fnArgs.rettype);
      }
    }
    typeStr = '<span class="' + cls + (useDetailedArgHints ? "typeHeader" : "typeHeader-simple") + '">' + typeStr + '</span>'; //outer wrapper
    if (useDetailedArgHints) {
      if (activeParam && activeParam.description) {
        typeStr += '<div class="' + cls + 'farg-current-description"><span class="' + cls + 'farg-current-name">' + activeParam.name + ': </span>' + activeParam.description + '</div>';
      }
      if (activeParamChildren && activeParamChildren.length > 0) {
        for (var i = 0; i < activeParamChildren.length; i++) {
          var t = activeParamChildren[i].type ? '<span class="' + cls + 'type">{' + activeParamChildren[i].type + '} </span>' : '';
          typeStr += '<div class="' + cls + 'farg-current-description">' + t + '<span class="' + cls + 'farg-current-name">' + getParamDetailedName(activeParamChildren[i]) + ': </span>' + activeParamChildren[i].description + '</div>';
        }
      }
    }
    tip.appendChild(elFromString(typeStr));
  }


  if (isNaN(parseInt(activeArg))) {
    if (data.doc) {
      var replaceParams = function (str, params) {
        if (params.length === 0) {
          return str;
        }
        str = str.replace(/@param/gi, '@param'); //make sure all param tags are lowercase
        var beforeParams = str.substr(0, str.indexOf('@param'));
        while (str.indexOf('@param') !== -1) {
          str = str.substring(str.indexOf('@param') + 6); //starting after first param match
        }
        if (str.indexOf('@') !== -1) {
          str = str.substr(str.indexOf('@')); //start at next tag that is not a param
        }
        else {
          str = ''; //@param was likely the last tag, trim remaining as its likely the end of a param description
        }
        var paramStr = '';
        for (var i = 0; i < params.length; i++) {
          paramStr += '<div>';
          if (params[i].parentName.trim() === '') {
            paramStr += ' <span class="' + cls + 'jsdoc-tag">@param</span> ';
          }
          else {
            paramStr += '<span class="' + cls + 'jsdoc-tag-param-child">&nbsp;</span> '; //dont show param tag for child param
          }
          paramStr += params[i].type.trim() === '' ? '' : '<span class="' + cls + 'type">{' + params[i].type + '}</span> ';

          if (params[i].name.trim() !== '') {
            var name = params[i].name.trim();
            if (params[i].parentName.trim() !== '') {
              name = params[i].parentName.trim() + '.' + name;
            }
            var pName = '<span class="' + cls + 'jsdoc-param-name">' + name + '</span>';
            if (params[i].defaultValue.trim() !== '') {
              pName += '<span class="' + cls + 'jsdoc-param-defaultValue">=' + params[i].defaultValue + '</span>';
            }
            if (params[i].optional) {
              pName = '<span class="' + cls + 'jsdoc-param-optionalWrapper">' + '<span class="' + cls + 'farg-optionalBracket">[</span>' + pName + '<span class="' + cls + 'jsdoc-param-optionalBracket">]</span>' + '</span>';
            }
            paramStr += pName;
          }
          paramStr += params[i].description.trim() === '' ? '' : ' - <span class="' + cls + 'jsdoc-param-description">' + params[i].description + '</span>';
          paramStr += '</div>';
        }
        if (paramStr !== '') {
          str = '<span class="' + cls + 'jsdoc-param-wrapper">' + paramStr + '</span>' + str;
        }

        return beforeParams + str;
      };
      var highlighTags = function (str) {
        try {
          str = ' ' + str + ' '; //add white space for regex
          var re = / ?@\w{1,50}\s ?/gi;
          var m;
          while ((m = re.exec(str)) !== null) {
            if (m.index === re.lastIndex) {
              re.lastIndex++;
            }
            str = str.replace(m[0], ' <span class="' + cls + 'jsdoc-tag">' + m[0].trim() + '</span> ');
          }
        }
        catch (ex) {
          showError(ts, editor, ex);
        }
        return str.trim();
      };
      var highlightTypes = function (str) {
        str = ' ' + str + ' '; //add white space for regex
        try {
          var re = /\s{[^}]{1,50}}\s/g;
          var m;
          while ((m = re.exec(str)) !== null) {
            if (m.index === re.lastIndex) {
              re.lastIndex++;
            }
            str = str.replace(m[0], ' <span class="' + cls + 'type">' + m[0].trim() + '</span> ');
          }
        }
        catch (ex) {
          showError(ts, editor, ex);
        }
        return str.trim();
      };
      var createLinks = function (str) {
        try {
          var httpProto = 'HTTP_PROTO_PLACEHOLDER';
          var httpsProto = 'HTTPS_PROTO_PLACEHOLDER';
          var re = /\bhttps?:\/\/[^\s<>"`{}|\^\[\]\\]+/gi;
          var m;
          while ((m = re.exec(str)) !== null) {
            if (m.index === re.lastIndex) {
              re.lastIndex++;
            }
            var withoutProtocol = m[0].replace(/https/i, httpsProto).replace(/http/i, httpProto);
            var text = m[0].replace(new RegExp('https://', 'i'), '').replace(new RegExp('http://', 'i'), '');
            str = str.replace(m[0], '<a class="' + cls + 'tooltip-link" href="' + withoutProtocol + '" target="_blank">' + text + ' </a>');
          }
          str = str.replace(new RegExp(httpsProto, 'gi'), 'https').replace(new RegExp(httpProto, 'gi'), 'http');
        }
        catch (ex) {
          showError(ts, editor, ex);
        }
        return str;
      };

      if (d.substr(0, 1) === '*') {
        d = d.substr(1); //tern leaves this for jsDoc as they start with /**, not exactly sure why...
      }
      d = htmlEncode(d.trim());
      d = replaceParams(d, params);
      d = highlighTags(d);
      d = highlightTypes(d);
      d = createLinks(d);
      tip.appendChild(elFromString(d));
    }
    if (data.url) {
      tip.appendChild(document.createTextNode(" "));
      var link = elt("a", null, "[more]");
      link.target = "_blank";
      link.href = data.url;
      // without this, clicking on link sometimes doesn't work
      link.addEventListener("mousedown", function(event) {
            event.stopPropagation();
            event.preventDefault();
      });
      tip.appendChild(link);
    }
    if (data.origin) {
      tip.appendChild(elt("div", null, elt("em", null, "source: " + data.origin)));
    }
  }

  return tip;
}

function parseFnType(text) {
  if (text.substring(0, 2) !== 'fn') return null; //not a function
  if (text.indexOf('(') === -1) return null;
  var args = [], pos = 3;

  function skipMatching(upto) {
    var depth = 0, start = pos;
    for (;;) {
      var next = text.charAt(pos);
      if (upto.test(next) && !depth) return text.slice(start, pos);
      if (/[{\[\(]/.test(next)) ++depth;
      else if (/[}\]\)]/.test(next)) --depth;
      ++pos;
    }
  }

  // Parse arguments
  if (text.charAt(pos) != ")") for (;;) {
    var name = text.slice(pos).match(/^([^, \(\[\{]+): /);
    if (name) {
      pos += name[0].length;
      name = name[1];
    }
    args.push({name: name, type: skipMatching(/[\),]/)});
    if (text.charAt(pos) == ")") break;
    pos += 2;
  }

  var rettype = text.slice(pos).match(/^\) -> (.*)$/);

  return {args: args, rettype: rettype && rettype[1]};
}

// Maintaining argument hints
function updateArgHints(ts, cm) {
  //console.log("updateArgHints!!!");
  closeArgHints(ts);

  if (cm.somethingSelected()) return;
  var state = cm.getTokenAt(cm.getCursor()).state;
  var inner = CodeMirror.innerMode(cm.getMode(), state);
  if (inner.mode.name != "javascript") return;
  var lex = inner.state.lexical;
  if (lex.info != "call") return;

  var ch, argPos = lex.pos || 0, tabSize = cm.getOption("tabSize");
  for (var line = cm.getCursor().line, e = Math.max(0, line - 9), found = false; line >= e; --line) {
    var str = cm.getLine(line), extra = 0;
    for (var pos = 0;;) {
      var tab = str.indexOf("\t", pos);
      if (tab == -1) break;
      extra += tabSize - (tab + extra) % tabSize - 1;
      pos = tab + 1;
    }
    ch = lex.column - extra;
    if (str.charAt(ch) == "(") {found = true; break;}
  }
  if (!found) return;

  var start = Pos(line, ch);
  var cache = ts.cachedArgHints;
  if (cache && cache.doc == cm.getDoc() && cmpPos(start, cache.start) == 0)
    return showArgHints(ts, cm, argPos);

  ts.request(cm, {type: "type", preferFunction: true, end: start}, function(error, data) {
    if (error || !data.type || !(/^fn\(/).test(data.type)) return;
    ts.cachedArgHints = {
      start: start,
      type: parseFnType(data.type),
      name: data.exprName || data.name || "fn",
      guess: data.guess,
      doc: cm.getDoc(),
      comments: data.doc //added by morgan- include comments with arg hints
    };
    showArgHints(ts, cm, argPos);
  });
}

function showArgHints(ts, editor, pos) {
  //console.log("showArgHints!");
  closeArgHints(ts);

  var cache = ts.cachedArgHints,
      tp = cache.type,
      comments = cache.comments; //added by morgan to include document comments
  if (!cache.hasOwnProperty('params')) {
    if (!cache.comments) {
      cache.params = null;
    }
    else {
      var params = parseJsDocParams(cache.comments);
      if (!params || params.length === 0) {
        cache.params = null;
      }
      else {
        cache.params = params;
      }
    }
  }

  var data = {
    name: cache.name,
    guess: cache.guess,
    fnArgs: cache.type,
    doc: cache.comments,
    params: cache.params
  };
  var tip = completionTip(data, pos);
  var place = editor.cursorCoords(null, "page");
  ts.activeArgHints = makeTooltip(place.left, place.top, tip);
}
