var CodeMirror = function() {}
CodeMirror.on = function(event, func){}
CodeMirror.getValue = function(){}
CodeMirror.getDoc = function(){}
CodeMirror.getDoc.prototype.setValue = function(code){}

CodeMirror.TernServer = function(props){}
CodeMirror.TernServer.prototype.complete = function(cm){}
CodeMirror.TernServer.prototype.showDocs = function(cm){}
CodeMirror.TernServer.prototype.showType = function(cm){}
CodeMirror.TernServer.prototype.jumpToDef = function(cm){}
CodeMirror.TernServer.prototype.jumpBack = function(cm){}
CodeMirror.TernServer.prototype.rename = function(cm){}
CodeMirror.TernServer.prototype.updateArgHints = function(cm){}
var completionTip = function(){}
var updateArgHints = function(){}

var Clipboard = function(elem, settings) {}

var splitMe = {}
splitMe.init = function(){}

var Sortable = {}
Sortable.create = function(elem, settings) {}

var SortableOnEndEvent = {}
SortableOnEndEvent.prototype.oldIndex = function() {};
SortableOnEndEvent.prototype.newIndex = function() {};