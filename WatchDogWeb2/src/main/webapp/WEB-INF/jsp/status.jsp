<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : Server Status</title>
<jsp:include page="header.jsp"></jsp:include>

</head>
<body>

	<div class="container">
		<div class="row" style="padding-left: 150px;">
			<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					<li class="breadcrumb-item active" aria-current="page">Status Info</li>
				</ol>
			</nav>
			<div class="col-sm-10">
				<table class="table-striped table" style="width: 80%">
					<tbody>
						<tr>
							<td>Watch Dog</td>
							<td><a href="javascript:void(0)" onclick="closeWatchDog()"><button class="btn btn-danger">Stop Me</button> </a></td>
						</tr>
						<tr>
							<td>${statusList[0].serverStatus}</td>
							<td><span class="btn btn-success">${statusList[1].serverStatus}</span></td>
						</tr>
						<tr>
							<td>Restore Config backup</td>
							<td><a href="javascript:void(0)" onclick="restoreWatchDog()"><span class="btn btn-info">Restore</span></a></td>
						</tr>
						<tr>
							<td>${statusList[0].hostAddress}</td>
							<td>${statusList[1].hostAddress}</td>
						</tr>
						<tr>
							<td>${statusList[0].hostName}</td>
							<td>${statusList[1].hostName}</td>
						</tr>
						<tr>
							<td>${statusList[0].cononicalHostName}</td>
							<td>${statusList[1].cononicalHostName}</td>
						</tr>
						<tr>
							<td>${statusList[0].userName}</td>
							<td>${statusList[1].userName}</td>
						</tr>

					</tbody>

				</table>
			</div>
		</div>
	</div>

</body>
<script type="text/javascript">
$(document).ready(function(){
     $("#tab1").addClass("active");
     /* $('#example1').DataTable( {
         "paging":   false,
         "ordering": false,
         "info":     false
     } ); */
    
});

function closeWatchDog(val) {
		var message = "Are you sure want to stop WatchDog?";
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
					window.location.href = "shutdownContext";
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
	
//width: 600,  height: 500,  modal: true,
function restoreWatchDog(val) {
	var message = "Are you sure want to restore database?";
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
				window.location.href = "restoreconfig";
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
