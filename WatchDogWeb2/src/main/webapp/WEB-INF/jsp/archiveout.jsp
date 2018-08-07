<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title>Watch Dog : Archive Folder Page</title>
<jsp:include page="header.jsp"></jsp:include>
</head>
<body>

	<div class="container">


		<div class="row" style="padding-left: 100px;">
			<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					<li class="breadcrumb-item active" aria-current="page">Archive Directory</li>
				</ol>
			</nav>
			<div class="col-sm-10">
			    <div id='alertId' class='alert alert-success' data-dismiss='alert' aria-label='Close' role='alert'>${param.msg}
			    <span style='float:right;cursor: pointer;'>&times;</span></div>
			
				<table id="example" class="display" style="width: 100%">
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

									<div class="btn-group btn-group-sm" role="group"
										aria-label="Basic example">
										<A href="downloadfile/${dt.fileName}/ar" title="Download File"
											title='Download File'>
											<button type="button"  class="btn_link btn-primary">View</button>
										</A> 
										 <A href="movetoinputdir/${dt.fileName}/arc" title='Move to Input Directory'>
											<button type="button"  class="btn_link btn-success">Move</button>
										</A>
									</div>
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
	$(document).ready(function() {
		$("#tab5").addClass("active");
		$("#tab1").removeClass("active");
		var v='${param.msg}';
		if(v==''){
			$("#alertId").hide();
		}
	});
</script>
</html>
