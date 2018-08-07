<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>
<title>Watch Dog : Reports</title>
<jsp:include page="header.jsp"></jsp:include>
<link rel="stylesheet" type="text/css" href="/css/buttons.dataTables.min.css">
<script type="text/javascript"  src="/js/dataTables.buttons.min.js"></script>
<script type="text/javascript"  src="/js/buttons.flash.min.js"></script>
<script type="text/javascript"  src="/js/jszip.min.js"></script>
<script type="text/javascript"  src="/js/pdfmake.min.js"></script>
<script type="text/javascript"  src="/js/vfs_fonts.js"></script>
<script type="text/javascript"  src="/js/buttons.html5.min.js"></script>
<script type="text/javascript"  src="/js/buttons.print.min.js"></script>
<script type="text/javascript" class="init">
	$(document).ready(function() {
		
		$('#reportsTable').DataTable({
			dom : 'Bfrtip',
			buttons : [ 'excel', 'pdf', 'print' ],
			"pageLength": 15,
	        "processing": true,

		
		});

		
	});
</script>
</head>
<body>
	<div class="container">
		<div class="row" style="padding-left: 50px;">
		<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					<li class="breadcrumb-item active" aria-current="page">Reports</li>
				</ol>
			</nav>
			<div class="col-sm-10">
												    
		            	<div class="form-group">

						<c:choose>
							<c:when test="${fn:length(dirList) ge 2}">
							    <label for="reportsBackupFile">Select Backup Reports</label>
								<select name="reportsBackupFile" onchange="getReports()" class="form-control" id="reportsId" >
									<c:forEach items="${dirList}" var="dt" varStatus="loop">
										<option value="${dirList[loop.index]}">${dirList[loop.index]}</option>
									</c:forEach>
								</select>
							</c:when>
						</c:choose>
					<br><br>
				</div>
						
				<table id="reportsTable" class="display nowrap" style="width: 100%">
					<thead>
						<tr>
							<th>#</th>
							<th>File Name</th>
							<th>Description</th>
							<th>Time</th>
							<th>Status</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${infoObj}" var="dt" varStatus="count">
							<tr>
								<td>${count.count}</td>
								<td>${dt.filename}</td>
								<td>${dt.description}</td>
								<td>${dt.filedat}</td>
								<td>
								<c:choose>
						            <c:when test="${dt.status eq 'FAIL'}">
										<button type="button"  class="btn_link btn-danger">&nbsp;Failed&nbsp;</button>
										
						            </c:when>
						            <c:otherwise>
						              <button type="button"  class="btn_link btn-success">Passed</button>
						              
						            </c:otherwise>
						        </c:choose>
								
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
$("#tab7").addClass("active");
$("#tab1").removeClass("active");

function getReports(){
	var reportsDb=$("#reportsId").val();
    window.location.href="reportspage?db="+reportsDb;
    
}
$(window).on('load', function() {
	var reportsDb='${param.db}';
	if(reportsDb!=''){
		$("#reportsId").val(reportsDb);
	}
});



</script>
</html>
