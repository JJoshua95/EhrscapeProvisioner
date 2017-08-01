app.controller('MainController', ['$scope', '$window', '$http', function($scope, $window, $http) { 
	
	$scope.username = '';
	$scope.password = '';
	$scope.baseUrl = '';
	
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
		console.log($scope.baseUrl);
		var body = JSON.stringify({username: $scope.username, password:$scope.password, baseUrl: $scope.baseUrl})
		console.log(body);
		$http.post("http://localhost:8080/EhrscapeProvisioner/api/provision/single-provision", body)
		.then(function(response) {
            var res = JSON.stringify(response);
            //$window.alert(res);
            console.log(res);
        });
	};
	
	$scope.provisionSingleWithDemographic = function() {
		console.log($scope.username);
		console.log($scope.password);
		console.log($scope.baseUrl);
		var body = JSON.stringify({username: $scope.username, password:$scope.password, baseUrl: $scope.baseUrl})
		console.log(body);
		$http.post("http://localhost:8080/EhrscapeProvisioner/api/provision/single-provision-with-demographic", body)
		.then(function(response) {
            var res = JSON.stringify(response);
            //$window.alert(res);
            console.log(res);
        });
	};
	
}]);

	