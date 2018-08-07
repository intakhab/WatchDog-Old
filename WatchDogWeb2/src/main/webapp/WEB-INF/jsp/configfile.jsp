<!DOCTYPE html>
<%@ page errorPage="errorpage.jsp" %>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title>Watch Dog : File Config Page</title>
<jsp:include page="header.jsp"></jsp:include>
<script  type="text/javascript" src="/js/typecomplete.js"></script>
<!-- <script  src="/js/jquery-ui.js"></script>
<link rel="stylesheet" href="/css/jquery-ui.css"> -->

</head>
<body>

	<div class="container">
	
		<div style="padding-left: 150px;">
			<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					    <li><a href="configfile">File Config </a></li>
					    <li class="breadcrumb-item active" aria-current="page"> Configuration Information for service</li>
				</ol>
			</nav>
			<div class="col-sm-9">
			    <div>
			      ${msg}
			      
				</div>
				<ul class="nav nav-tabs" id="navId">
				    <li class="active" onclick="chckTab('filePathId1')" id="filePathId1"><a data-toggle="tab" href="#filePathId" title="Dog Configuration">Dog Config</a></li>
				    <li id="batchPathId1" onclick="chckTab('batchPathId1')"><a data-toggle="tab" href="#batchPathId" title="Financial Configuration">Fin Config</a></li>
				    <li id="loadConfigId1" onclick="chckTab('loadConfigId1')"><a data-toggle="tab" href="#loadConfigId" title="NonEdi Configuration">NonEdi Config</a></li>
				    <li id="fileConfigId1"onclick="chckTab('fileConfigId1')"><a data-toggle="tab" href="#fileConfigId" title="Others Configuration">Others Config</a></li>
				    <li id="planIdTab2"onclick="chckTab('planIdTab2')"><a data-toggle="tab" href="#planIdTab" title="Plan Id">Plan ID</a></li>

			   </ul>
				<form:form name="form" role="form" method="POST" id="configForm"  action="saveconfiginfo" modelAttribute="infoObj">
				<form:hidden path="dogId" />
				
				 <div class="tab-content">
				       <br>
				     <!-- Start Tab 1 -->   
				    <div id="filePathId" class="tab-pane active">
						  <div class="form-group">
								<label for="batchFilePath">Batch File Path</label>
								<form:input path="batchFilePath"  class="form-control" id="batchFilePathId" placeholder="Batch File Path"
									required="required" />
							</div>
							<div class="form-group">
								<label for="inputFolderPath">Input Folder Path</label>
								<form:input path="inputFolderPath" class="form-control" id="inputFolderPathId" placeholder="Input Folder Path"
									required="required" />
							</div>
							<div class="form-group">
								<label for="outputFolderPath">Output Folder Path</label>
								<form:input path="outputFolderPath" class="form-control" id="outputFolderPathId" placeholder="Ouptput Folder Path"
									required="required" />
							</div>
							<div class="form-group">
								<label for="archiveFolderPath">Archive Folder Path</label>
								<form:input path="archiveFolderPath" cssClass="form-control" id="archiveFolderPathId" placeholder="Archive Folder Path" required="required" />
							</div>
		                     
		                     <div class="form-group">
								<label for="archiveFolderPath">Failure Folder Path</label>
								<form:input path="failureFolderPath" cssClass="form-control" id="failureFolderPath" placeholder="Failure Folder Path" required="required" />
							</div>
							<div class="form-group">
								<label for="pollingTime">File Support(s)</label>
								<form:input path="fileSupports" class="form-control"
									id="fileSupportsId" placeholder="Enter File Type" required="required" />
								<small id="fileSupportsSmall" class="form-text text-muted">Can
									write file end with comma separator like <strong>cam,dc,so,edi,ssc</strong></small>
		
							</div>
							<div id="mydiv" class="">
						    <div class="form-group">
							
									<div class="form-group input_fields_wrap">
									   
										<label class="" for="action_id">Change Entity Name</label>
										<small id="actionIdName" class="form-text text-muted"> 
										You can write like <strong>cam=changeEntity</strong> 
										  (Don't put whitespace) 15 Maximum input boxes allowed. 
										</small>
										<c:choose >
										<c:when test="${fn:length(infoObj.supportsAPI) eq 0}">
         									  <div class="entry input-group">
												<form:input id="action_id" path="supportsAPI[0]"
													placeholder="Enter like cam=changeEntity"
													class="form-control input-md" required="required" title="${infoObj.supportsAPI[0]}" />

												<span class="input-group-btn">
													<button id="add-more" name="add-more"
														class="btn btn-success add-more" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br>
         								</c:when>
										<c:otherwise>
									    <c:forEach items="${infoObj.supportsAPI}" varStatus="loop">
											<div class="entry input-group">
												<form:input id="action_id" path="supportsAPI[${loop.index}]"
													placeholder="Enter like cam=changeEntity"
													class="form-control input-md" title="${infoObj.supportsAPI[loop.index]}"  />

												<span class="input-group-btn">
													<button id="add-more" name="add-more"
														class="btn btn-success add-more" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>

											</div>
											<br />
											</c:forEach>
										</c:otherwise>
								</c:choose>
										
								</div>
								</div>
								
								<br> 
				        	</div>
                        </div>
                         <!-- End Tab 1 -->
                         <!-- Start Tab 2 -->
                        <div  id="batchPathId" class="tab-pane">
		                        	<div class="form-group">
								<label for="optInputFolderPath">Fin Input Folder Path</label>
								<form:input path="optInputFolderPath" class="form-control" id="optInputFolderPathId" placeholder="Input Folder Path"
									required="required" />
							</div>
							<div class="form-group">
								<label for="optOutputFolderPath">Fin Output Folder Path</label>
								<form:input path="optOutputFolderPath" class="form-control" id="optOutputFolderPathId" placeholder="Ouptput Folder Path"
									required="required" />
							</div>
							
							<div class="form-group">
								<label for="optArchiveFolderPath">Fin Archive Folder Path</label>
								<form:input path="optArchiveFolderPath" cssClass="form-control" id="optArchiveFolderPathId" placeholder="Archive Folder Path" required="required" />
							</div>
							<div class="form-group">
								<label for="optFailureFolderPath">Fin Failure Folder Path</label>
								<form:input path="optFailureFolderPath" class="form-control" id="optFailureFolderPathId" placeholder="Failure Folder Path"
									required="required" />
							</div>
		                     <div class="form-group">
								<label for="optFileSupports">Fin File Supports</label>
								<form:input path="optFileSupports" class="form-control"
									id="fileSupportsId" placeholder="Enter File Type" required="required" />
								<small id="fileSupportsSmall" class="form-text text-muted">Can
									write file end with comma separator like <strong>cam,dc,so</strong></small>
		
							</div>
				    	<div id="mydiv">
						    <div class="form-group">
									<div class="form-group input_fields_wrap1">
										<label class="" for="action_id">Fin API Run</label>
										<small id="actionIdName" class="form-text text-muted"> 
										ex. <strong>API@{TAG=VAL|TAG=VAL}</strong> if you want to put value like TAG=?
										  processScheduleRunRequest@{ScheduleRunTypeEnumVal=SRT_APVCHR|RequestID=?}
										  (Don't put whitespace)
										</small>
										<c:choose >
										<c:when test="${fn:length(infoObj.optSupportsAPI) eq 0}">
         									  <div class="entry input-group">
												<form:input id="action_id" path="optSupportsAPI[0]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value} if you dont know value then write value=?"
													class="form-control input-md" title="${infoObj.optSupportsAPI[0]}"/>

												<span class="input-group-btn">
													<button id="add-more1" name="add-more"
														class="btn btn-success add-more1" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
								               

											</div>
											<br>
         								</c:when>
										<c:otherwise>
									    <c:forEach items="${infoObj.optSupportsAPI}" varStatus="loop">
											<div class="entry input-group">
												<form:input id="action_id" path="optSupportsAPI[${loop.index}]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value} if you dont know value then write value=?"
													class="form-control input-md" title="${infoObj.optSupportsAPI[loop.index]}"/>

												<span class="input-group-btn">
													<button id="add-more1" name="add-more"
														class="btn btn-success add-more1" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>

											</div>
											<br />
											</c:forEach>
										</c:otherwise>
								</c:choose>
										
								</div>
								</div>
								
								<br> 
					       </div>
                        
                  </div>
                    <div  id="loadConfigId" class="tab-pane">
		                     <div class="form-group">
								<label for="nonEdiCamFileSupports">Non Edi CaM File Supports</label>
								<form:input path="nonEdiCamFileSupports" class="form-control"
									id="nonEdiCamFileSupports" placeholder="Enter File Type" required="required" />
								<small id="fileSupportsSmall" class="form-text text-muted">Can
									write file end with comma separator like <strong>cam,dc,so</strong></small>
		
							</div>
				    	<div id="mydiv">
						    <div class="form-group">
									<div class="form-group input_fields_wrap2">
										<label for="action_id">Non Edi CaM API Run</label>
										<small id="actionIdName" class="form-text text-muted"> 
										ex. <strong>API@{TAG=VAL|TAG=VAL}</strong> if you want to put no value the write BLANK
										  (Don't put whitespace)
										</small>
										<c:choose >
										<c:when test="${fn:length(infoObj.nonEdiCamSupportsAPI) eq 0}">
         									  <div class="entry input-group">
												<form:input id="action_id" path="nonEdiCamSupportsAPI[0]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.nonEdiCamSupportsAPI[0]}"/>

												<span class="input-group-btn">
													<button id="add-more1" name="add-more"
														class="btn btn-success add-more2" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
								               

											</div>
											<br>
         								</c:when>
										<c:otherwise>
									    <c:forEach items="${infoObj.nonEdiCamSupportsAPI}" varStatus="loop">
											<div class="entry input-group">
												<form:input id="action_id" path="nonEdiCamSupportsAPI[${loop.index}]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.nonEdiCamSupportsAPI[loop.index]}"/>

												<span class="input-group-btn">
													<button id="add-more1" name="add-more"
														class="btn btn-success add-more2" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>

											</div>
											<br />
											</c:forEach>
										</c:otherwise>
								</c:choose>
										
								</div>
								</div>
								
								<br> 
					       </div>
                        
                  </div>
                  <!-- End Tab 2 -->
                  <!-- Start Tab 3 -->
				<div id="fileConfigId" class="tab-pane">
					<div class="form-group">
					   <input type="hidden" id="checkBoxId" value="${infoObj.enableMail}"/>
						<form:checkbox path="enableMail" value="${enableMail}" class="form-check-input"
							 id="emailCheckedId"/>
							
							 
						<label class="form-check-label" for="emailCheckedId">Enable Email </label>
						<div id="emailCheckedTextAreaId" >
							<div class="input-group">
								<span class="input-group-addon">@</span>
								<form:input type="email" path="toWhomEmail" class="form-control" 
									id="toWhomEmailId" placeholder="Send Email ias@hcl.com,foo@hcl.com" 
									required="required" aria-describedby="toWhomEmailIns" multiple="multiple"/>
							</div>
							<small id="toWhomEmailIns" class="form-text text-muted">Can
								Write email name with comma separator i.e ias@hcl.com, foo@hcl.com</small>
						</div>

					</div>
                  

					<div class="form-row row">
						<div class="form-group col-md-6">
							<label for="responseLog">Enable response log</label>
							<form:select path="enableResponseCodeLog" class="form-control"
								id="enableResponseLogId">
								<form:option value="true">true</form:option>
								<form:option value="false">false</form:option>

							</form:select>
						</div>
						<div class="form-group col-md-6">
							<label for="enableArchiveOthersFile">Enable Archive which is not valid file</label>
							<form:select path="enableArchiveOthersFile" class="form-control"
								id="enableArchiveOthersFileId">
								<form:option value="true">true</form:option>
								<form:option value="false">false</form:option>

							</form:select>
						</div>
					</div>

					<div class="form-row row">
						<div class="form-group col-md-6">
							<label for="fileTypeSeparator">File Separator</label>
							<form:input path="fileTypeSeparator" value="@"
								class="form-control" id="fileTypeSeparatorId"
								placeholder="File Separator"  required="required" readonly="readonly" />
								<small id="fileTypeSeparator" class="form-text text-muted">You can't change, it is read only</small>
						</div>
						<div class="form-group col-md-6">
							<label for="fileExtension">File Extension</label>
							<form:input path="fileExtension" value="xml" class="form-control"
								id="fileExtensionId" placeholder="Enter File Extension" required="required" readonly="readonly" />
							<small id="fileExtensionId" class="form-text text-muted">You can't change, it is read only</small>	
						</div>
					</div>

					<div class="form-row row">
						<div class="form-group col-md-6">
							<label for="limitFilesFolder">Limit files in folder</label>
							
							<form:input path="limitFilesFolder" 
								class="form-control" id="limitFilesFolderId"
								placeholder="Limit files in folder" required="required" />
						</div>
						
						<div class="form-group col-md-6">
							<label for="responseFilePrefix">Out XML File Prefix</label>
							<form:input path="responseFilePrefix" 
								class="form-control" id="responseFilePrefixId"
								placeholder="Enter File Extension" required="required" />
						</div>
					</div>
					<div class="form-row row">
					<div class="form-group col-md-4">
							<label for="stopFileRun">Dog Batch Run</label>
							<form:select path="stopFileRun" class="form-control"
								id="stopFileRunId" title="You can start and stop Dog Batch Run">
								<form:option value="true">Stop</form:option>
								<form:option value="false">Start</form:option>

							</form:select>
						<small id="tt" class="form-text text-muted">This is for testing purpose only.</small>	
						</div>
						<div class="form-group col-md-4">
							<label for="stopBatchRun">Fin Batch Run</label>
							<form:select path="stopBatchRun" class="form-control"
								id="stopBatchRunId" title="You can start and stop Fin Batch Run">
								<form:option value="true">Stop</form:option>
								<form:option value="false">Start</form:option>

							</form:select>
						<small id="tt" class="form-text text-muted">This is for testing purpose only.</small>	
						</div>
						
						<div class="form-group col-md-4">
							<label for="stopNonEdiBatchRun">NonEdi Batch Run</label>
							<form:select path="stopNonEdiBatchRun" class="form-control"
								id="stopNonEdiBatchRun" title="You can start and stop NonEdi Batch Run">
								<form:option value="true">Stop</form:option>
								<form:option value="false">Start</form:option>

							</form:select>
						<small id="tt" class="form-text text-muted">This is for testing purpose only.</small>	
						</div>
						
					</div>	
				 <br>

				 </div>
				  <!-- End Tab 3 -->
				  <!-- Tab 4 -->
				   <div  id="planIdTab" class="tab-pane">
				   
				   <div class="col-md-10">
		                   	<div class="form-group ptop">
								<label for="optInputFolderPath">Enter Plan ID </label>
								<form:input path="systemPlanIdText" id="systemPlanIdText" class="form-control typeahead tt-query" autocomplete="off"
								 spellcheck="false" placeholder="Enter Plan Id here" required="required"/>
							   <form:hidden path="systemPlanId" id="systemPlanIdArray"/>
							</div>
							<br/>
							<br/>
							
							<table id="planIdTable" class="display" style="width: 100%">

							</table>
						   <div style="padding-top: 80px;">
					     
					     </div>
						</div>
					    
					 </div>
					 
					 <!-- Tab 4 End -->
					 
					 
					   
					</div>
					<button type="submit" id="submitButtonId" class="btn btn-primary">Save File Configuration</button>
					<button type="button" id="re-loadId" class="btn btn-success">Reload Page</button>
					
					<br />
					<br />
					<br />
					<br />
				
				</form:form>
  			
			</div>
		</div>
	</div>

	<div>
	
</div>



</body>
<script type="text/javascript">
	$(document).ready(function() {
		$("#tab2").addClass("active");
		$("#tab1").removeClass("active");
		$("#re-loadId").click(function(e) {
			window.location.href="configfile";

		});
        
		////////////
		
	    var max_fields      = 15; //maximum input boxes allowed
	    var wrapper         = $(".input_fields_wrap"); //Fields wrapper
	    var add_button      = $(".add-more"); //Add button ID

	    var x = 1; //initlal text box count
	    $(add_button).click(function(e){ //on add input button click
	        e.preventDefault();
	        if(x < max_fields){ //max input box allowed
	            x++; //text box increment
	            $(wrapper).append('<div class="form-group"><div class="entry input-group"><label for="action_id"></label><input name="supportsAPI['+x+']" id="supportsAPI['+x+']" type="text" class="form-control input-md" required="required"/><span class="input-group-btn"><button class="btn btn-danger remove_field" type="button"><span class="glyphicon glyphicon-minus"></span></button></span></div></div>'); //add input box
	            legnth++;
	        }
	    });
	    
	    $(wrapper).on("click",".remove_field", function(e){ //user click on remove text
	           e.preventDefault(); 
	           $(this).parent('span').parent('div').remove();x--;
	          // $(this).parent('div').remove(); 
	    })
	    
	    $(window).bind("load", function() {
	    	if($("#checkBoxId").val().trim()==true || $("#checkBoxId").val().trim()=="true"){
	    	     $('#emailCheckedTextAreaId').show();
	    	     $('#emailCheckedId').prop('checked', true);
				 $('#toWhomEmailId').prop('required',true);

	    	}else{
		    	 $('#emailCheckedTextAreaId').hide();
		    	 $('#emailCheckedId').prop('checked' ,false)
				 $('#toWhomEmailId').prop('required',false);
	    	}
	   	}); 
	    
	    $("#emailCheckedId").click(function(e) {
			if ($('#emailCheckedId').prop('checked')) {
				$('#emailCheckedTextAreaId').show();
				$('#toWhomEmailId').prop('required',true);
			} else {
				$('#emailCheckedTextAreaId').hide();
				$('#toWhomEmailId').val("");
			    $('#toWhomEmailId').prop('required',false);
			}
		});
	    $('#submitButtonId').click(function () {
	        $('input:invalid').each(function () {
	            // Find the tab-pane that this element is inside, and get the id
	            var $closest = $(this).closest('.tab-pane');
	            var id = $closest.attr('id');
	            // Find the link that corresponds to the pane and have it show
	            $('.nav a[href="#' + id + '"]').tab('show');
				$("#filePathId1").removeClass("active");
				$("#batchPathId1").removeClass("active");
				$("#fileConfigId1").removeClass("active");
				$("#loadConfigId1").removeClass("active");

				$("#"+id+"1").addClass("active");
					// Only want to do it once
					return false;
				});
			});

		});//end
		
		function chckTab(id){
			$("#filePathId1").removeClass("active");
			$("#batchPathId1").removeClass("active");
			$("#fileConfigId1").removeClass("active");
			$("#planIdTab2").removeClass("active");
			$("#loadConfigId1").removeClass("active");
			$("#"+id).addClass("active");

	    }
		
		$(document).ready(function() {
		    var max_fields      = 15; //maximum input boxes allowed
		    var wrapper         = $(".input_fields_wrap1"); //Fields wrapper
		    var add_button      = $(".add-more1"); //Add button ID
		    var x = 1; //initlal text box count
		    $(add_button).click(function(e){ //on add input button click
		        e.preventDefault();
		        if(x < max_fields){ //max input box allowed
		            x++; //text box increment
		            $(wrapper).append('<div class="form-group"><div class="entry input-group"><label for="action_id"></label><input name="optSupportsAPI['+x+']" id="optSupportsAPI['+x+']" type="text" class="form-control input-md" required="required"/><span class="input-group-btn"><button class="btn btn-danger remove_field1" type="button"><span class="glyphicon glyphicon-minus"></span></button></span></div></div>'); //add input box
		            legnth++;
		        }
		    });
		    
		    $(wrapper).on("click",".remove_field1", function(e){ //user click on remove text
		           e.preventDefault(); 
		           $(this).parent('span').parent('div').remove();x--;
		          // $(this).parent('div').remove(); 
		    })
 
		    window.setTimeout(function() {
		        $(".alert").fadeTo(500, 0).slideUp(500, function(){
		            $(this).remove(); 
		            window.location.href='configfile';
		        });
		    }, 500);
		
		
        });
		//load
		$(document).ready(function() {
		    var max_fields      = 15; //maximum input boxes allowed
		    var wrapper         = $(".input_fields_wrap2"); //Fields wrapper
		    var add_button      = $(".add-more2"); //Add button ID
		    var x = 1; //initlal text box count
		    $(add_button).click(function(e){ //on add input button click
		        e.preventDefault();
		        if(x < max_fields){ //max input box allowed
		            x++; //text box increment
		            $(wrapper).append('<div class="form-group"><div class="entry input-group"><label for="action_id"></label><input name="nonEdiCamSupportsAPI['+x+']" id="nonEdiCamSupportsAPI['+x+']" type="text" class="form-control input-md" required="required"/><span class="input-group-btn"><button class="btn btn-danger remove_field2" type="button"><span class="glyphicon glyphicon-minus"></span></button></span></div></div>'); //add input box
		            legnth++;
		        }
		    });
		    
		    $(wrapper).on("click",".remove_field2", function(e){ //user click on remove text
		           e.preventDefault(); 
		           $(this).parent('span').parent('div').remove();x--;
		          // $(this).parent('div').remove(); 
		    })
		    
		
        });
		
</script>
<script type="text/javascript">
$(document).ready(function(){
    // Defining the local dataset
    var suggestion = $("#systemPlanIdArray").val().split(",");
    // Constructing the suggestion engine
    var pid = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.whitespace,
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        local: suggestion
    });
    
    // Initializing the typeahead
    $('.typeahead').typeahead({
        hint: true,
        highlight: true, /* Enable substring highlighting */
        minLength: 1 /* Specify minimum characters required for showing result */
    },
    {
        name: 'pid',
        source: pid
    });
    var dataSet = new Array();

     dataSet='['
        $.each(suggestion, function( index, value ) {
    	 // dataSet+='['+ (index+1) +','+ value +'],'
      	  dataSet+='['+ value +'],'

    	});
       dataSet = dataSet.substring(0, dataSet.length - 1); //
       dataSet=dataSet +']';
      
   var table= $('#planIdTable').DataTable( {
        data: JSON.parse(dataSet),
        dom: 'Bfrtip',
        "scrollY": "200px",
         select: true,
        columns: [
            { title: "PlanID" }
        ],
        "columnDefs": [ {
            "targets": 1,
            "data": null,
            "defaultContent": "<a href='javascript:void()' id='selId' class='btn_link btn-info'>&nbsp;&nbsp;&nbsp;Select Plan ID&nbsp;&nbsp;&nbsp;</a>"
        }
        /* ,
        {
		       "targets": 2,
		       "data": null,
		       "defaultContent": "<a href='javascript:void()' id='delId' class='btn_link btn-danger'>Delete</a>"
		   }  */
        ]
    } );
   
   /*  var tableVal="";
	 $('#planIdTable tbody').on( 'click', 'tr', function () {
	  	    tableVal=table.row( this ).data();

 	 }); */
	 
   $('#planIdTable tbody').on( 'click', 'tr', function () {
	   var tableVal=table.row( this ).data();
	      var message="Are you sure want to change Plan Id?";
	     $('<div></div>').appendTo('body')
         .html('<div><h6>'+message+'?</h6></div>')
         .dialog({
             modal: true, title: 'Confirmation', zIndex: 10000, autoOpen: true,
             width: 'auto', resizable: false,
             buttons: {
                 Yes: function () {
                     $(this).dialog("close");
             	      $("#systemPlanIdText").val(tableVal)
                 },
                 No: function () {                           		                      
                     $(this).dialog("close");
                 }
             },
             close: function (event, ui) {
                 $(this).remove();
             }
         });

	});	

   $( "#configForm" ).submit(function( event ) {
	   event.preventDefault();
	   var message="Are you sure want to Save the Configuration?";
	     $('<div></div>').appendTo('body')
       .html('<div><h6>'+message+'</h6></div>')
       .dialog({
           modal: true, title: 'Confirmation', zIndex: 10000, autoOpen: true,
           width: 'auto', 
           resizable: false,
           buttons: {
               Yes: function () {
                   $(this).dialog("close");
                   document.getElementById("configForm").submit();

               },
               No: function () {                           		                      
                   $(this).dialog("close");
               }
           },
           close: function (event, ui) {
               $(this).remove();
           }
       });
     });

});  

</script>
</html>
