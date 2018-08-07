<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : Logs pages</title>
<jsp:include page="header.jsp"></jsp:include>
</head>
<body>

	<div class="container">

		<div class="row" style="padding-left: 150px;">
		<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					<li class="breadcrumb-item active" aria-current="page">Logs Folder</li>
				</ol>
			</nav>
			<div class="col-sm-10">
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
								<td><a href="downloadfile/${dt.fileName}/logs" title="Download Logs">
									<button type="button"  class="btn_link btn-primary">View</button>

								</a></td>
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
	  $("#tab9").addClass("active");
	  $("#tab1").removeClass("active");
});
</script>
</html>
