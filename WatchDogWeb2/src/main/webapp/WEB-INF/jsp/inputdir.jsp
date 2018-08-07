<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : Input Folder</title>
<jsp:include page="header.jsp"></jsp:include>
<jsp:include page="model.jsp"></jsp:include>
<script  src="/js/jquery-ui.js"></script>
<link rel="stylesheet" href="/css/jquery-ui.css">
</head>
<body>

	<div class="container">

		<div class="row" style="padding-left: 5px;">
		   <nav aria-label="breadcrumb" style="width: 100%">
				<ol class="breadcrumb">
					<li class="breadcrumb-item active" aria-current="page">Input Directory</li>
				</ol>
			</nav>
			<div class="col-sm-12">
						   <div id='alertId' class='alert alert-success' data-dismiss='alert' aria-label='Close' role='alert'>${param.msg}
			    <span style='float:right;cursor: pointer;'>&times;</span></div>
			
				<table id="example" class="display"
					style="width: 100%">
					<thead>
						<tr>
							<th></th>
							<th>File Name</th>
							<th>Last Modified Date</th>
							<th>#File Type</th>
							<th>#File End With</th>
							<th></th>
							
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${fileList}" var="dt">
							<tr>
								<td>${dt.id}</td>
								<td>${dt.fileName}</td>
								<td>${dt.lastModifiedDate}</td>
								<td>${dt.fileStatus}</td>
								<td title="Check File Naming Convention" class="btn-link">
			   					 <c:choose>
						            <c:when test="${dt.fileStatus eq 'INVALID FILE'}">
						               ${dt.fileEndWith}
						            </c:when>
						            <c:otherwise>
						               
						            </c:otherwise>
						        </c:choose>
								
								</td>
								<td>
								<A href="downloadfile/${dt.fileName}/in"  title="Download & Rename File with Valid naming convention and Upload it." 
								>
								 
								<button type="button"  class="btn_link btn-primary">View</button>
								</A>
								
								<c:choose>
						            <c:when test="${dt.fileStatus eq 'INVALID FILE'}">
						             <a id="renameId" href="javascript:void(0)">
										<button type="button"  class="btn_link btn-success">Rename File</button>
									</a> 
						              <input type="hidden" id="fileNameId" value="${dt.fileName}"/>
						            </c:when>
						            <c:otherwise>
						               
						            </c:otherwise>
						        </c:choose>
								    
								
                                <a  href="javascript:void(0)"  onclick="deleteFile('${dt.fileName}')">
                               
                             	   <button type="button"  class="btn_link btn-danger">Delete</button>
                                 </a>
                                  <a href="movetoinputdir/${dt.fileName}/in" title='Move to Archive Directory'>
											<button type="button"  class="btn_link btn-success">Move</button>
								</a>
                                </td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div style="padding-top: 100px;"></div>
	</div>

</body>
<script type="text/javascript">
$(document).ready(function(){
	$("#renameId").click(function(){
		
    	    var fileEndWith= $("#fileEndWithId").val();
    	    var fileNameId=$("#fileNameId").val();
    	    if(fileEndWith==''){
    		    var message = "File is not supporting, Please rename it <br/> Select File Type dropdown to rename file";
	    		$('<div></div>').appendTo('body').html(
	    				'<div><h6>' + message + '</h6></div>').dialog({
		    			modal : true,
		    			title : 'Information',
		    			zIndex : 10000,
		    			autoOpen : true,
		    			width : 'auto',
		    			resizable : false,
		    			buttons:{
			    			No : function() {
								$(this).dialog("close");
							}
		    			},
		    			close : function(event, ui) {
		    				$(this).remove();
		    	    	    $("#fileEndWithId").focus()
		
		    			}
	    		});
    	    }else{
    	    	window.location.href="renameinvalidfile/"+fileNameId+"/"+fileEndWith;
    	    }
	});
	
     $("#tab3").addClass("active");
     $("#tab1").removeClass("active");
     var v='${param.msg}';
		if(v==''){
			$("#alertId").hide();
		}
	});
	
	function deleteFile(val) {	
		var message = "Are you sure want to delete file?";
		$('<div></div>').appendTo('body').html(
				'<div><h6>' + message + '</h6></div>').dialog({
			modal : true,
			title : 'Confirmation',
			zIndex : 10000,
			autoOpen : true,
			width : 'auto',
			resizable : false,
			buttons : {
				Yes : function() {
					$(this).dialog("close");
					window.location.href = "deletefilefrominput/" + val;
				},
				No : function() {
					$(this).dialog("close");
				}
			},
			close : function(event, ui) {
				$(this).remove();
			}
		});
	}
</script>
</html>
