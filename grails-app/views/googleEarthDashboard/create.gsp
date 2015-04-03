<%--
Copyright (C) 2008-2010 United States Government. All rights reserved. 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
--%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<meta name="layout" content="main" />
		<title>RAGE: Create Dashboard KML</title>
		<g:javascript src="jquery/jquery-1.8.3.js" />
		<g:javascript src="jquery/plugins/farbtastic-1.1.js" />
		<g:javascript src="jquery/plugins/validate-1.6.js" />
		<link rel="stylesheet" href="${createLinkTo(dir:'css',file:'farbtastic.css')}" type="text/css" />
		<script type="text/javascript">
			$(document).ready(
				function () {
					$.validator.addClassRules("latitude", {range:[-90,90]});
					$.validator.addClassRules("longitude", {range:[-180,180]});
					
					addInputRow();
					$("#kmlDashboard").validate();
					$("#colorpicker").farbtastic().hide();
					//$("[rel='tooltip']").tooltip(); //activate tooltips
				}
			);

			function addInputRow() {
				var jInputsContainer = getInputsContainer();

				//Get input(s) template and make copy of template
				var jInputsTemplate = $("#feed-input-template");
				var jInputs = jInputsTemplate.clone();
				var strNewHtml = jInputs.html();

				//Replace tags with row specific value
				var intInputRowCount = (getNumInputRows() + 1);
				strNewHtml = strNewHtml.replace(/::INPUTID::/g, intInputRowCount);

				//Replace jquery required class name tag with class name.  Needed
				//so validate function will not attempt to validate template!
				strNewHtml = strNewHtml.replace(/::jquery_req_class::/g, "required");

				strNewHtml = strNewHtml.replace(/#000000/g, getRandomColor());

				//Append new input template to DOM
				jInputsContainer.append(strNewHtml);

				//Need to link color wheel so input displays color
				linkColorWheel(intInputRowCount);
			}

			function removeLastInputRow() {
				var intLastRowNum = getNumInputRows();
				if (intLastRowNum == 1) {
					//alert("Cannot delete last feed input row!");
				} else {
					var jInputRowToDelete = $("#inputRow" + getNumInputRows());
					jInputRowToDelete.remove();
				}
			}

			function getRandomColor() {
				var color = "#";
				for (i=0; i<6; i++) {
					var randomValue = Math.floor(Math.random()*16);
					var hexValue = 0;
					switch (randomValue) {
						case 10:
							hexValue = "a";
							break;
						case 11:
							hexValue = "b";
							break;
						case 12:
							hexValue = "c";
							break;
						case 13:
							hexValue = "d";
							break;
						case 14:
							hexValue = "e";
							break;
						case 15:
							hexValue = "f";
							break;
						default:
							hexValue = randomValue;
					}
					color = color + hexValue;
				}
				return color;
			}			

			//Get ref to inputs container DOM object
			function getInputsContainer() {
				return $("#feeds");
			}

			function getNumInputRows() {
				var jInputsContainer = getInputsContainer();
				return jInputsContainer.find("div.inputRow").length;
			}

			function showColorWheel(inputRowNum) {
				$("#colorpicker").show();
				linkColorWheel(inputRowNum);
			}

			function linkColorWheel(inputRowNum) {
				var farbtasticPicker = $.farbtastic("#colorpicker");
				var jColorInput =  $("#feedColor" + inputRowNum);
				farbtasticPicker.linkTo(jColorInput);
			}

			function hideColorWheel() {
				$("#colorpicker").hide();
			}
			
			function toggleAdvandedOptions(inputRowNum) {
				if ("none" == $("#advancedOptions" + inputRowNum).css('display')) {
					$("#advancedOptions" + inputRowNum).show();
					//$("#advancedOptionsToggle" + inputRowNum).text("^");
				} else {
					$("#advancedOptions" + inputRowNum).hide();
					//$("#advancedOptionsToggle" + inputRowNum).text("v");
				}
			}

			function toggleTitleFilter(inputRowNum) {
				if ($("#titleFilterCheckbox" + inputRowNum).is(':checked')) {
					$("#titleOperator" + inputRowNum).removeAttr('disabled');
					$("#titleFilter" + inputRowNum).removeAttr('disabled');
				} else {
        			$("#titleOperator" + inputRowNum).attr('disabled', true);
        			$("#titleFilter" + inputRowNum).attr('disabled', true);
				}   
			}
			
			function toggleContentFilter(inputRowNum) {
				if ($("#contentFilterCheckbox" + inputRowNum).is(':checked')) {
					$("#contentOperator" + inputRowNum).removeAttr('disabled');
					$("#contentFilter" + inputRowNum).removeAttr('disabled');
				} else {
        			$("#contentOperator" + inputRowNum).attr('disabled', true);
        			$("#contentFilter" + inputRowNum).attr('disabled', true);
				}
			}
			
			function toggleNumericalFilter(inputRowNum) {
				if ($("#numericalFilterCheckbox" + inputRowNum).is(':checked')) {
					$("#numericalFilterVariable" + inputRowNum).removeAttr('disabled');
					$("#numericalOperator" + inputRowNum).removeAttr('disabled');
					$("#numericalFilterOperand" + inputRowNum).removeAttr('disabled');
					$("#numericalField" + inputRowNum).removeAttr('disabled');
				} else {
        			$("#numericalFilterVariable" + inputRowNum).attr('disabled', true);
        			$("#numericalOperator" + inputRowNum).attr('disabled', true);
        			$("#numericalFilterOperand" + inputRowNum).attr('disabled', true);
        			$("#numericalField" + inputRowNum).attr('disabled', true);
				}
			}

			function toggleBoundingBoxFilter(inputRowNum) {
				if ($("#boundingBoxFilterCheckbox" + inputRowNum).is(':checked')) {
					$("#upperLeftLat" + inputRowNum).removeAttr('disabled');
					$("#upperLeftLon" + inputRowNum).removeAttr('disabled');
					$("#lowerRightLat" + inputRowNum).removeAttr('disabled');
					$("#lowerRightLon" + inputRowNum).removeAttr('disabled');
				} else {
        			$("#upperLeftLat" + inputRowNum).attr('disabled', true);
        			$("#upperLeftLon" + inputRowNum).attr('disabled', true);
        			$("#lowerRightLat" + inputRowNum).attr('disabled', true);
					$("#lowerRightLon" + inputRowNum).attr('disabled', true);
				}
			}
			
			function setActionAndSubmit(action) {
				$('#kmlDashboard').attr('action', action);
				$('#kmlDashboard').submit();
			}
		</script>
	</head>
	<body>
		<div class="container">
			<div class="row">
				<div class="span12">
			    	<h2>Visualize</h2>
    			</div>
    		</div>
		
			<div class="row">
				<div class="span12">
			    	<div class="alert alert-info">
			    		<p>
							<strong>Paste</strong> your web feed links in the form below. Click the colored box to customize map markers. 
							Use the <button class="btn btn-mini btn-info"><i class="icon-plus icon-white"></i></button> button to subscribe to multiple feeds and the <button class="btn btn-mini btn-info"><i class="icon-minus icon-white"></i></button> button to remove unneeded row(s). 
							Click the <button class="btn btn-mini btn-info"><i class="icon-filter icon-white"></i></button> button to reduce data noise with feed filtering.
						</p>
						<p>
							<strong>When finished</strong>, click <button class="btn btn-mini btn-primary"><i class="icon-globe icon-white"></i> View in Google Earth</button> to view your web feeds in Google Earth.
							<g:if test="${true == grailsApplication.config.rage.map.enable}">
							Click <button class="btn btn-mini btn-primary"><i class="icon-picture icon-white"></i> View in Map</button> to view your web feeds in your browser with an integrated timeline/map.
							</g:if>
						</p>
					</div>
    			</div>
    		</div>
				
			<div class="row">
				<div class="span10">
					<div class="well">
						<form class="form-inline" name="kmlDashboard" id="kmlDashboard">
							<div class="control-group">
								<input type="text" name="folderName" class="input-mini" value="rage" /> <a href="#" rel="tooltip" data-toggle="tooltip" title="Input specifies the folder name in your Google Earth places tree."><i class="icon-question-sign"></i></a>
							</div>
							<div id="feeds">
								<!-- Contents added dynamically from copied template -->
							</div>
						</form>
						<div class="control-group">
							<button class="btn btn-mini btn-info" onClick="removeLastInputRow()"><i class="icon-minus icon-white"></i></button>
							<button class="btn btn-mini btn-info" onClick="addInputRow()"><i class="icon-plus icon-white"></i></button>
						</div>
						<div class="control-group">
							<button class="btn btn-primary" onClick="javascript:setActionAndSubmit('kml')"><i class="icon-globe icon-white"></i> View in Google Earth</button>
							<g:if test="${grailsApplication.config.rage.map.enable}">
								<button class="btn btn-primary" onClick="javascript:setActionAndSubmit('timemap')"><i class="icon-picture icon-white"></i> View in Map</button>
							</g:if>
						</div>
					</div>
				</div>
				<div class="span2">
					<div id="colorpicker"></div>
				</div>
			</div>
				
				
				<!-- DOM Template. Note: '::INPUTID::' tags replaced with specific row number to provied unique ids-->
				<div id="feed-input-template" style="display:none;">
					<div id="inputRow::INPUTID::" class="inputRow control-group">
						<input type="text" id="feedColor::INPUTID::" name="feedColor::INPUTID::" value="#000000" 
							class="input-mini" class="::jquery_req_class:: rgbColor"
 							onfocus="showColorWheel('::INPUTID::')"
							onblur="hideColorWheel()" />
						<label for="feedUrl::INPUTID::">Feed Url</label>
						<input type="text" name="feedUrl::INPUTID::" class="::jquery_req_class:: url input-xxlarge" />
						<button class="btn btn-mini btn-info" id="advancedOptionsToggle::INPUTID::" type="button" onclick="toggleAdvandedOptions('::INPUTID::')"><i class="icon-filter icon-white"></i></button>
						
						<div id="advancedOptions::INPUTID::" style="display:none; margin-left:50px;">
							<p>Filter entries by ALL of the following that are checked</p>
							<input type="checkbox" id="titleFilterCheckbox::INPUTID::" name="titleFilterCheckbox::INPUTID::" onchange="toggleTitleFilter('::INPUTID::')" />
							<label>title</label>&nbsp; &nbsp; &nbsp;
							<select id="titleOperator::INPUTID::" name="titleOperator::INPUTID::" class="input-medium" disabled>
								<option value="EQUALS">is</option>
								<option value="NOTEQUALS">is not</option>
								<option value="CONTAINS">contains</option>
								<option value="NOTCONTAINS">does not contain</option>
							</select>
							<input type="text" id="titleFilter::INPUTID::" name="titleFilter::INPUTID::" size="50" disabled />
							<br/>
							
							<input type="checkbox" id="contentFilterCheckbox::INPUTID::" name="contentFilterCheckbox::INPUTID::" onchange="toggleContentFilter('::INPUTID::')" />
							<label>content</label>
							<select id="contentOperator::INPUTID::" name="contentOperator::INPUTID::" class="input-medium" disabled>
								<option value="EQUALS">is</option>
								<option value="NOTEQUALS">is not</option>
								<option value="CONTAINS">contains</option>
								<option value="NOTCONTAINS">does not contain</option>
							</select>
							<input type="text" id="contentFilter::INPUTID::" name="contentFilter::INPUTID::" size="50" disabled/>
							<br/>
							
							<input type="checkbox" id="numericalFilterCheckbox::INPUTID::" name="numericalFilterCheckbox::INPUTID::" onchange="toggleNumericalFilter('::INPUTID::')" />
							<label>numerical</label>
							<input type="text" id="numericalFilterVariable::INPUTID::" name="numericalFilterVariable::INPUTID::" class="input-mini" disabled/>
							<select id="numericalOperator::INPUTID::" name="numericalOperator::INPUTID::" class="input-mini"disabled>
								<option value="EQUALS">=</option>
								<option value="NOTEQUALS">!=</option>
								<option value="LESSTHAN">&#60;</option>
								<option value="GREATERTHAN">&#62;</option>
							</select>
							<input type="text" id="numericalFilterOperand::INPUTID::" name="numericalFilterOperand::INPUTID::" class="number input-mini" disabled/>
							in
							<select id="numericalField::INPUTID::" name="numericalField::INPUTID::" class="input-small" disabled>
								<option value="TITLE">title</option>
								<option value="CONTENT">content</option>
							</select>
							<br/>
							
							<input type="checkbox" id="boundingBoxFilterCheckbox::INPUTID::" name="boundingBoxFilterCheckbox::INPUTID::" onchange="toggleBoundingBoxFilter('::INPUTID::')" />
							<label>bounding box</label>
							<fieldset style="width:300px; margin-left:50px;">
								<legend>Upper Left:</legend>
								Lat: <input type="text" id="upperLeftLat::INPUTID::" name="upperLeftLat::INPUTID::" class="latitude input-mini" disabled/>
								Lon: <input type="text" id="upperLeftLon::INPUTID::" name="upperLeftLon::INPUTID::" class="longitude input-mini" disabled/>
							</fieldset>
							<fieldset style="width:300px; margin-left:50px;">
								<legend>Lower Right:</legend>
								Lat: <input type="text" id="lowerRightLat::INPUTID::" name="lowerRightLat::INPUTID::" class="latitude input-mini" disabled/>
								Lon: <input type="text" id="lowerRightLon::INPUTID::" name="lowerRightLon::INPUTID::" class="longitude input-mini" disabled/>
							</fieldset>
						</div>
					</div>
					
				</div>
		</div>
        </div>
	</body>
</html>
