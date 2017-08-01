app.controller('MainController', ['$scope', '$window', '$http', function($scope, $window, $http) { 
	
	$scope.username = '';
	$scope.password = '';
	
	$scope.testFunction = function() {
		$window.alert("Clicked!");
	};
	
	$scope.getSession = function() {
		$http.post("http://localhost:8080/EhrscapeProvisioner/api/myresource/getSession")
		.then(function(response) {
            var res = JSON.stringify(response);
            //$window.alert(res);
            console.log(res);
        });
	};
	
	$scope.provisionSingle = function() {
		console.log($scope.username);
		console.log($scope.password);
		var body = JSON.stringify({username: $scope.username, password:$scope.password})
		console.log(body);
		$http.post("http://localhost:8080/EhrscapeProvisioner/api/provision/single-provision", body)
		.then(function(response) {
            var res = JSON.stringify(response);
            //$window.alert(res);
            console.log(res);
        });
	};
	
}]);

	