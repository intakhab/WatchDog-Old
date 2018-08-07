<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : Failure Folder</title>
<jsp:include page="header.jsp"></jsp:include>
</head>
<body>

	<div class="container">

		<div class="row" style="padding-left: 50px;">
		<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					<li class="breadcrumb-item active" aria-current="page">Failure Folder</li>
				</ol>
			</nav>
			<div class="col-sm-10">
				  <div id='alertId' class='alert alert-success' data-dismiss='alert' aria-label='Close' role='alert'>${param.msg}
			    <span style='float:right;cursor: pointer;'>&times;</span></div>
			
				<table id="example" class="display"
					style="width: 100%">
					<thead>
						<tr>
							<th></th>
							<th>File Name</th>
							<th>Last Modified Date</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${fileList}" var="dt">
							<tr>
								<td>${dt.id}</td>
								<td>${dt.fileName}</td>
								<td>${dt.lastModifiedDate}</td>
								<td>
								<a href="downloadfile/${dt.fileName}/fail" title="Download Fail XML">
									<button type="button"  class="btn_link btn-primary">View</button>

								</a>
								<a href="movetoinputdir/${dt.fileName}/fail" title='Move to Input Directory'>
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
	
     $("#tab8").addClass("active");
     $("#tab1").removeClass("active");
     var v='${param.msg}';
		if(v==''){
			$("#alertId").hide();
		}
});
</script>
</html>
