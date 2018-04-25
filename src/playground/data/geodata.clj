(ns playground.data.geodata
  (:require [cheshire.core :as json]))


(def data {:custom        {:world_source {:name "World Source"}, :world {:name "World"}, :russia {:name "Russia"}},
           :usa_states    {:wisconsin      {:name "Wisconsin"},
                           :vermont        {:name "Vermont"},
                           :florida        {:name "Florida"},
                           :illinois       {:name "Illinois"},
                           :kentucky       {:name "Kentucky"},
                           :montana        {:name "Montana"},
                           :west_virginia  {:name "West Virginia"},
                           :pennsylvania   {:name "Pennsylvania"},
                           :texas          {:name "Texas"},
                           :oklahoma       {:name "Oklahoma"},
                           :michigan       {:name "Michigan"},
                           :connecticut    {:name "Connecticut"},
                           :new_hampshire  {:name "New Hampshire"},
                           :mississippi    {:name "Mississippi"},
                           :north_carolina {:name "North Carolina"},
                           :oregon         {:name "Oregon"},
                           :utah           {:name "Utah"},
                           :louisiana      {:name "Louisiana"},
                           :new_jersey     {:name "New Jersey"},
                           :minnesota      {:name "Minnesota"},
                           :iowa           {:name "Iowa"},
                           :delaware       {:name "Delaware"},
                           :arizona        {:name "Arizona"},
                           :north_dakota   {:name "North Dakota"},
                           :california     {:name "California"},
                           :georgia        {:name "Georgia"},
                           :massachusetts  {:name "Massachusetts"},
                           :idaho          {:name "Idaho"},
                           :nebraska       {:name "Nebraska"},
                           :missouri       {:name "Missouri"},
                           :arkansas       {:name "Arkansas"},
                           :colorado       {:name "Colorado"},
                           :wyoming        {:name "Wyoming"},
                           :ohio           {:name "Ohio"},
                           :alabama        {:name "Alabama"},
                           :hawaii         {:name "Hawaii"},
                           :tennessee      {:name "Tennessee"},
                           :rhode_island   {:name "Rhode Island"},
                           :new_mexico     {:name "New Mexico"},
                           :nevada         {:name "Nevada"},
                           :virginia       {:name "Virginia"},
                           :kansas         {:name "Kansas"},
                           :maryland       {:name "Maryland"},
                           :south_dakota   {:name "South Dakota"},
                           :south_carolina {:name "South Carolina"},
                           :alaska         {:name "Alaska"},
                           :indiana        {:name "Indiana"},
                           :washington     {:name "Washington"},
                           :new_york       {:name "New York"},
                           :maine          {:name "Maine"}},
           :china_areas   {:gansu        {:name "Gansu"},
                           :guangdong    {:name "Guangdong"},
                           :shaanxi      {:name "Shaanxi"},
                           :xizang       {:name "Xizang"},
                           :zhejiang     {:name "Zhejiang"},
                           :tianjin      {:name "Tianjin"},
                           :jilin        {:name "Jilin"},
                           :qinghai      {:name "Qinghai"},
                           :fujian       {:name "Fujian"},
                           :shanghai     {:name "Shanghai"},
                           :hunan        {:name "Hunan"},
                           :henan        {:name "Henan"},
                           :beijing      {:name "Beijing"},
                           :hainan       {:name "Hainan"},
                           :anhui        {:name "Anhui"},
                           :shandong     {:name "Shandong"},
                           :ningxia      {:name "Ningxia"},
                           :guangxi      {:name "Guangxi"},
                           :liaoning     {:name "Liaoning"},
                           :hubei        {:name "Hubei"},
                           :jiangxi      {:name "Jiangxi"},
                           :sichuan      {:name "Sichuan"},
                           :shanxi       {:name "Shanxi"},
                           :chongqing    {:name "Chongqing"},
                           :hebei        {:name "Hebei"},
                           :guizhou      {:name "Guizhou"},
                           :jiangsu      {:name "Jiangsu"},
                           :yunnan       {:name "Yunnan"},
                           :inner_mongol {:name "Inner Mongol"},
                           :tibet        {:name "Tibet"},
                           :heilongjiang {:name "Heilongjiang"}},
           :canada_states {:quebec                    {:name "Quebec"},
                           :yukon                     {:name "Yukon"},
                           :british_columbia          {:name "British Columbia"},
                           :prince_edward_island      {:name "Prince Edward Island"},
                           :nova_scotia               {:name "Nova Scotia"},
                           :saskatchewan              {:name "Saskatchewan"},
                           :northwest_territories     {:name "Northwest Territories"},
                           :ontario                   {:name "Ontario"},
                           :nunavut                   {:name "Nunavut"},
                           :newfoundland_and_labrador {:name "Newfoundland And Labrador"},
                           :new_brunswick             {:name "New Brunswick"},
                           :manitoba                  {:name "Manitoba"},
                           :alberta                   {:name "Alberta"}},
           :countries     {:djibouti                         {:name "Djibouti"},
                           :cameroon                         {:name "Cameroon"},
                           :china                            {:name "China"},
                           :vanuatu                          {:name "Vanuatu"},
                           :brunei                           {:name "Brunei"},
                           :hungary                          {:name "Hungary"},
                           :new_caledonia                    {:name "New Caledonia"},
                           :colombia                         {:name "Colombia"},
                           :armenia                          {:name "Armenia"},
                           :france                           {:name "France"},
                           :dominica                         {:name "Dominica"},
                           :papua_new_guinea                 {:name "Papua New Guinea"},
                           :kenya                            {:name "Kenya"},
                           :algeria                          {:name "Algeria"},
                           :singapore                        {:name "Singapore"},
                           :senegal                          {:name "Senegal"},
                           :azerbaijan                       {:name "Azerbaijan"},
                           :east_timor                       {:name "East Timor"},
                           :belarus                          {:name "Belarus"},
                           :namibia                          {:name "Namibia"},
                           :south_africa                     {:name "South Africa"},
                           :anguilla                         {:name "Anguilla"},
                           :republic_of_the_congo            {:name "Republic Of The Congo"},
                           :australia                        {:name "Australia"},
                           :liberia                          {:name "Liberia"},
                           :lesotho                          {:name "Lesotho"},
                           :montserrat                       {:name "Montserrat"},
                           :kuwait                           {:name "Kuwait"},
                           :united_arab_emirates             {:name "United Arab Emirates"},
                           :macedonia                        {:name "Macedonia"},
                           :argentina                        {:name "Argentina"},
                           :zimbabwe                         {:name "Zimbabwe"},
                           :estonia                          {:name "Estonia"},
                           :bulgaria                         {:name "Bulgaria"},
                           :germany                          {:name "Germany"},
                           :guyana                           {:name "Guyana"},
                           :montenegro                       {:name "Montenegro"},
                           :s._sudan                         {:name "S. Sudan"},
                           :san_marino                       {:name "San Marino"},
                           :niger                            {:name "Niger"},
                           :malawi                           {:name "Malawi"},
                           :morocco                          {:name "Morocco"},
                           :philippines                      {:name "Philippines"},
                           :switzerland                      {:name "Switzerland"},
                           :belgium                          {:name "Belgium"},
                           :central_african_republic         {:name "Central African Republic"},
                           :ukraine                          {:name "Ukraine"},
                           :suriname                         {:name "Suriname"},
                           :burkina_faso                     {:name "Burkina Faso"},
                           :chad                             {:name "Chad"},
                           :madagascar                       {:name "Madagascar"},
                           :benin                            {:name "Benin"},
                           :jordan                           {:name "Jordan"},
                           :sri_lanka                        {:name "Sri Lanka"},
                           :honduras                         {:name "Honduras"},
                           :solomon_islands                  {:name "Solomon Islands"},
                           :guinea                           {:name "Guinea"},
                           :mali                             {:name "Mali"},
                           :ireland                          {:name "Ireland"},
                           :costa_rica                       {:name "Costa Rica"},
                           :luxembourg                       {:name "Luxembourg"},
                           :cyprus                           {:name "Cyprus"},
                           :angola                           {:name "Angola"},
                           :kyrgyzstan                       {:name "Kyrgyzstan"},
                           :bolivia                          {:name "Bolivia"},
                           :czech_republic                   {:name "Czech Republic"},
                           :oman                             {:name "Oman"},
                           :botswana                         {:name "Botswana"},
                           :israel                           {:name "Israel"},
                           :austria                          {:name "Austria"},
                           :bhutan                           {:name "Bhutan"},
                           :slovakia                         {:name "Slovakia"},
                           :iran                             {:name "Iran"},
                           :afghanistan                      {:name "Afghanistan"},
                           :united_states_of_america         {:name "United States Of America"},
                           :latvia                           {:name "Latvia"},
                           :iraq                             {:name "Iraq"},
                           :croatia                          {:name "Croatia"},
                           :gabon                            {:name "Gabon"},
                           :tajikistan                       {:name "Tajikistan"},
                           :lithuania                        {:name "Lithuania"},
                           :mexico                           {:name "Mexico"},
                           :saudi_arabia                     {:name "Saudi Arabia"},
                           :finland                          {:name "Finland"},
                           :peru                             {:name "Peru"},
                           :zambia                           {:name "Zambia"},
                           :turkmenistan                     {:name "Turkmenistan"},
                           :nepal                            {:name "Nepal"},
                           :ethiopia                         {:name "Ethiopia"},
                           :trinidad_and_tobago              {:name "Trinidad And Tobago"},
                           :turks_and_caicos_islands         {:name "Turks And Caicos Islands"},
                           :south_korea                      {:name "South Korea"},
                           :liechtenstein                    {:name "Liechtenstein"},
                           :guatemala                        {:name "Guatemala"},
                           :united_kingdom                   {:name "United Kingdom"},
                           :malaysia                         {:name "Malaysia"},
                           :greece                           {:name "Greece"},
                           :turkey                           {:name "Turkey"},
                           :venezuela                        {:name "Venezuela"},
                           :thailand                         {:name "Thailand"},
                           :libya                            {:name "Libya"},
                           :italy                            {:name "Italy"},
                           :mongolia                         {:name "Mongolia"},
                           :bosnia_and_herzegovina           {:name "Bosnia And Herzegovina"},
                           :sweden                           {:name "Sweden"},
                           :samoa                            {:name "Samoa"},
                           :paraguay                         {:name "Paraguay"},
                           :saint_pierre_and_miquelon        {:name "Saint Pierre And Miquelon"},
                           :denmark                          {:name "Denmark"},
                           :laos                             {:name "Laos"},
                           :the_bahamas                      {:name "The Bahamas"},
                           :andorra                          {:name "Andorra"},
                           :georgia                          {:name "Georgia"},
                           :japan                            {:name "Japan"},
                           :sudan                            {:name "Sudan"},
                           :republic_of_serbia               {:name "Republic Of Serbia"},
                           :poland                           {:name "Poland"},
                           :sierra_leone                     {:name "Sierra Leone"},
                           :iceland                          {:name "Iceland"},
                           :saint_vincent_and_the_grenadines {:name "Saint Vincent And The Grenadines"},
                           :democratic_republic_of_the_congo {:name "Democratic Republic Of The Congo"},
                           :haiti                            {:name "Haiti"},
                           :barbados                         {:name "Barbados"},
                           :saint_lucia                      {:name "Saint Lucia"},
                           :aland                            {:name "Aland"},
                           :slovenia                         {:name "Slovenia"},
                           :belize                           {:name "Belize"},
                           :bermuda                          {:name "Bermuda"},
                           :somalia                          {:name "Somalia"},
                           :el_salvador                      {:name "El Salvador"},
                           :malta                            {:name "Malta"},
                           :syria                            {:name "Syria"},
                           :ghana                            {:name "Ghana"},
                           :mozambique                       {:name "Mozambique"},
                           :egypt                            {:name "Egypt"},
                           :saint_kitts_and_nevis            {:name "Saint Kitts And Nevis"},
                           :uruguay                          {:name "Uruguay"},
                           :moldova                          {:name "Moldova"},
                           :hong_kong_s.a.r.                 {:name "Hong Kong S.a.r."},
                           :north_korea                      {:name "North Korea"},
                           :jamaica                          {:name "Jamaica"},
                           :nicaragua                        {:name "Nicaragua"},
                           :united_states_virgin_islands     {:name "United States Virgin Islands"},
                           :chile                            {:name "Chile"},
                           :myanmar                          {:name "Myanmar"},
                           :ecuador                          {:name "Ecuador"},
                           :dominican_republic               {:name "Dominican Republic"},
                           :qatar                            {:name "Qatar"},
                           :india                            {:name "India"},
                           :uganda                           {:name "Uganda"},
                           :cambodia                         {:name "Cambodia"},
                           :togo                             {:name "Togo"},
                           :uzbekistan                       {:name "Uzbekistan"},
                           :united_republic_of_tanzania      {:name "United Republic Of Tanzania"},
                           :vietnam                          {:name "Vietnam"},
                           :mauritania                       {:name "Mauritania"},
                           :brazil                           {:name "Brazil"},
                           :cuba                             {:name "Cuba"},
                           :greenland                        {:name "Greenland"},
                           :pakistan                         {:name "Pakistan"},
                           :nauru                            {:name "Nauru"},
                           :lebanon                          {:name "Lebanon"},
                           :gambia                           {:name "Gambia"},
                           :guinea_bissau                    {:name "Guinea Bissau"},
                           :rwanda                           {:name "Rwanda"},
                           :indonesia                        {:name "Indonesia"},
                           :kazakhstan                       {:name "Kazakhstan"},
                           :ivory_coast                      {:name "Ivory Coast"},
                           :romania                          {:name "Romania"},
                           :eritrea                          {:name "Eritrea"},
                           :swaziland                        {:name "Swaziland"},
                           :taiwan                           {:name "Taiwan"},
                           :albania                          {:name "Albania"},
                           :panama                           {:name "Panama"},
                           :canada                           {:name "Canada"},
                           :nigeria                          {:name "Nigeria"},
                           :spain                            {:name "Spain"},
                           :yemen                            {:name "Yemen"},
                           :burundi                          {:name "Burundi"},
                           :bangladesh                       {:name "Bangladesh"},
                           :tunisia                          {:name "Tunisia"}}})


(def data-1-0-0 {:custom    {:world {:name "World"}}
                 :countries {:djibouti                         {:name "Djibouti"}
                             :cameroon                         {:name "Cameroon"}
                             :china                            {:name "China"}
                             :vanuatu                          {:name "Vanuatu"}
                             :brunei                           {:name "Brunei"}
                             :hungary                          {:name "Hungary"}
                             :new_caledonia                    {:name "New Caledonia"}
                             :colombia                         {:name "Colombia"}
                             :armenia                          {:name "Armenia"}
                             :france                           {:name "France"}
                             :dominica                         {:name "Dominica"}
                             :papua_new_guinea                 {:name "Papua New Guinea"}
                             :kenya                            {:name "Kenya"}
                             :algeria                          {:name "Algeria"}
                             :singapore                        {:name "Singapore"}
                             :senegal                          {:name "Senegal"}
                             :azerbaijan                       {:name "Azerbaijan"}
                             :east_timor                       {:name "East Timor"}
                             :belarus                          {:name "Belarus"}
                             :namibia                          {:name "Namibia"}
                             :south_africa                     {:name "South Africa"}
                             :anguilla                         {:name "Anguilla"}
                             :republic_of_the_congo            {:name "Republic Of The Congo"}
                             :australia                        {:name "Australia"}
                             :liberia                          {:name "Liberia"}
                             :lesotho                          {:name "Lesotho"}
                             :montserrat                       {:name "Montserrat"}
                             :kuwait                           {:name "Kuwait"}
                             :united_arab_emirates             {:name "United Arab Emirates"}
                             :macedonia                        {:name "Macedonia"}
                             :argentina                        {:name "Argentina"}
                             :zimbabwe                         {:name "Zimbabwe"}
                             :estonia                          {:name "Estonia"}
                             :bulgaria                         {:name "Bulgaria"}
                             :germany                          {:name "Germany"}
                             :guyana                           {:name "Guyana"}
                             :montenegro                       {:name "Montenegro"}
                             :s._sudan                         {:name "S. Sudan"}
                             :san_marino                       {:name "San Marino"}
                             :niger                            {:name "Niger"}
                             :malawi                           {:name "Malawi"}
                             :morocco                          {:name "Morocco"}
                             :philippines                      {:name "Philippines"}
                             :switzerland                      {:name "Switzerland"}
                             :belgium                          {:name "Belgium"}
                             :central_african_republic         {:name "Central African Republic"}
                             :ukraine                          {:name "Ukraine"}
                             :suriname                         {:name "Suriname"}
                             :burkina_faso                     {:name "Burkina Faso"}
                             :chad                             {:name "Chad"}
                             :madagascar                       {:name "Madagascar"}
                             :benin                            {:name "Benin"}
                             :jordan                           {:name "Jordan"}
                             :sri_lanka                        {:name "Sri Lanka"}
                             :honduras                         {:name "Honduras"}
                             :solomon_islands                  {:name "Solomon Islands"}
                             :guinea                           {:name "Guinea"}
                             :mali                             {:name "Mali"}
                             :ireland                          {:name "Ireland"}
                             :costa_rica                       {:name "Costa Rica"}
                             :luxembourg                       {:name "Luxembourg"}
                             :cyprus                           {:name "Cyprus"}
                             :angola                           {:name "Angola"}
                             :kyrgyzstan                       {:name "Kyrgyzstan"}
                             :bolivia                          {:name "Bolivia"}
                             :czech_republic                   {:name "Czech Republic"}
                             :oman                             {:name "Oman"}
                             :botswana                         {:name "Botswana"}
                             :israel                           {:name "Israel"}
                             :austria                          {:name "Austria"}
                             :bhutan                           {:name "Bhutan"}
                             :slovakia                         {:name "Slovakia"}
                             :iran                             {:name "Iran"}
                             :afghanistan                      {:name "Afghanistan"}
                             :united_states_of_america         {:name "United States Of America"}
                             :latvia                           {:name "Latvia"}
                             :iraq                             {:name "Iraq"}
                             :croatia                          {:name "Croatia"}
                             :gabon                            {:name "Gabon"}
                             :tajikistan                       {:name "Tajikistan"}
                             :lithuania                        {:name "Lithuania"}
                             :mexico                           {:name "Mexico"}
                             :saudi_arabia                     {:name "Saudi Arabia"}
                             :finland                          {:name "Finland"}
                             :peru                             {:name "Peru"}
                             :zambia                           {:name "Zambia"}
                             :turkmenistan                     {:name "Turkmenistan"}
                             :nepal                            {:name "Nepal"}
                             :ethiopia                         {:name "Ethiopia"}
                             :trinidad_and_tobago              {:name "Trinidad And Tobago"}
                             :turks_and_caicos_islands         {:name "Turks And Caicos Islands"}
                             :south_korea                      {:name "South Korea"}
                             :liechtenstein                    {:name "Liechtenstein"}
                             :guatemala                        {:name "Guatemala"}
                             :united_kingdom                   {:name "United Kingdom"}
                             :malaysia                         {:name "Malaysia"}
                             :greece                           {:name "Greece"}
                             :turkey                           {:name "Turkey"}
                             :venezuela                        {:name "Venezuela"}
                             :thailand                         {:name "Thailand"}
                             :libya                            {:name "Libya"}
                             :italy                            {:name "Italy"}
                             :mongolia                         {:name "Mongolia"}
                             :bosnia_and_herzegovina           {:name "Bosnia And Herzegovina"}
                             :sweden                           {:name "Sweden"}
                             :samoa                            {:name "Samoa"}
                             :paraguay                         {:name "Paraguay"}
                             :saint_pierre_and_miquelon        {:name "Saint Pierre And Miquelon"}
                             :denmark                          {:name "Denmark"}
                             :laos                             {:name "Laos"}
                             :the_bahamas                      {:name "The Bahamas"}
                             :andorra                          {:name "Andorra"}
                             :georgia                          {:name "Georgia"}
                             :japan                            {:name "Japan"}
                             :sudan                            {:name "Sudan"}
                             :republic_of_serbia               {:name "Republic Of Serbia"}
                             :poland                           {:name "Poland"}
                             :sierra_leone                     {:name "Sierra Leone"}
                             :iceland                          {:name "Iceland"}
                             :saint_vincent_and_the_grenadines {:name "Saint Vincent And The Grenadines"}
                             :democratic_republic_of_the_congo {:name "Democratic Republic Of The Congo"}
                             :haiti                            {:name "Haiti"}
                             :barbados                         {:name "Barbados"}
                             :saint_lucia                      {:name "Saint Lucia"}
                             :aland                            {:name "Aland"}
                             :slovenia                         {:name "Slovenia"}
                             :belize                           {:name "Belize"}
                             :bermuda                          {:name "Bermuda"}
                             :somalia                          {:name "Somalia"}
                             :el_salvador                      {:name "El Salvador"}
                             :malta                            {:name "Malta"}
                             :syria                            {:name "Syria"}
                             :ghana                            {:name "Ghana"}
                             :mozambique                       {:name "Mozambique"}
                             :egypt                            {:name "Egypt"}
                             :saint_kitts_and_nevis            {:name "Saint Kitts And Nevis"}
                             :uruguay                          {:name "Uruguay"}
                             :moldova                          {:name "Moldova"}
                             :hong_kong_s.a.r.                 {:name "Hong Kong S.a.r."}
                             :north_korea                      {:name "North Korea"}
                             :jamaica                          {:name "Jamaica"}
                             :nicaragua                        {:name "Nicaragua"}
                             :united_states_virgin_islands     {:name "United States Virgin Islands"}
                             :chile                            {:name "Chile"}
                             :myanmar                          {:name "Myanmar"}
                             :ecuador                          {:name "Ecuador"}
                             :dominican_republic               {:name "Dominican Republic"}
                             :qatar                            {:name "Qatar"}
                             :india                            {:name "India"}
                             :uganda                           {:name "Uganda"}
                             :cambodia                         {:name "Cambodia"}
                             :togo                             {:name "Togo"}
                             :uzbekistan                       {:name "Uzbekistan"}
                             :united_republic_of_tanzania      {:name "United Republic Of Tanzania"}
                             :vietnam                          {:name "Vietnam"}
                             :mauritania                       {:name "Mauritania"}
                             :brazil                           {:name "Brazil"}
                             :cuba                             {:name "Cuba"}
                             :greenland                        {:name "Greenland"}
                             :pakistan                         {:name "Pakistan"}
                             :nauru                            {:name "Nauru"}
                             :lebanon                          {:name "Lebanon"}
                             :gambia                           {:name "Gambia"}
                             :guinea_bissau                    {:name "Guinea Bissau"}
                             :rwanda                           {:name "Rwanda"}
                             :indonesia                        {:name "Indonesia"}
                             :kazakhstan                       {:name "Kazakhstan"}
                             :ivory_coast                      {:name "Ivory Coast"}
                             :romania                          {:name "Romania"}
                             :eritrea                          {:name "Eritrea"}
                             :swaziland                        {:name "Swaziland"}
                             :taiwan                           {:name "Taiwan"}
                             :albania                          {:name "Albania"}
                             :panama                           {:name "Panama"}
                             :canada                           {:name "Canada"}
                             :nigeria                          {:name "Nigeria"}
                             :spain                            {:name "Spain"}
                             :yemen                            {:name "Yemen"}
                             :burundi                          {:name "Burundi"}
                             :bangladesh                       {:name "Bangladesh"}
                             :tunisia                          {:name "Tunisia"}}})


(defn gdata []
  (let [data (json/parse-string (slurp "/media/ssd/sibental/playground-data/MODULES V8 GENERATION/modules-8.1.0.json") true)
        data (:geodata data)]
    (println data)
    (println (keys data))
    data))
