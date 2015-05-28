/**
 * Το angular module που ακόλουθη είναι η έκδοση της ιστοσελίδας που καλύπτει
 * μόνο της ακόλουθες λειτουργίες: Δημιουργία ομάδας (Room). Διαγραφή
 * ομάδας(Room). Δημιουργία καινούριου εγγράφου (Documents). Ενημέρωση
 * εγγράφου(Documents). Παρουσίαση αλλαγών στα έγραφα των ROOMS που είναι μέλος
 * ο χρήστης Επιστροφή περιεχόμενον της πλατφόρμας είτε Room είτε Document
 */
var app=angular.module('webPlatformApp', ['ngRoute','steam','LocalStorageModule']);
app.config(['$routeProvider', '$locationProvider', function ($routeProvider,$locationProvider) {
$routeProvider.when('/', {
    templateUrl: 'views/index.html',
    controller: 'IndexCtrl',
    })
    .when('/index', {
    templateUrl: 'views/index.html',
    controller: 'IndexCtrl',
    })
    .when('/index.html', {
    templateUrl: 'views/index.html',
    controller: 'IndexCtrl',
    }).when('/notifications', {
    templateUrl: 'views/notifications.html',
    controller: 'notificationsCtrl',
    })
    .when('/editProfile', {
    templateUrl: 'views/editProfile.html',
    controller: 'editProfileCtrl',
    requireLogin: true
    })
    .when('/messages', {
    templateUrl: 'views/messages.html',
    controller: 'messagesCtrl',
    })
    .when('/adminPanel', {
    templateUrl: 'views/adminPanel.html',
    controller: 'adminPanelCtrl',
    })  
    .when("/home/",{
    templateUrl: 'views/home.html',
    controller: 'homeCtrl',
    })
    .when("/home/:paramiters*",{
    templateUrl: 'views/home.html',
    controller: 'homeCtrl',
    })
  
    .when("/home",{
    templateUrl: 'views/home.html',
    controller: 'homeCtrl',
    })
    .when("/error",{
    templateUrl: 'views/error.html',
    controller: 'errorCtrl',
    }).otherwise({ redirectTo: '/error' });
}]);
/**
 * O 'IndexCtrl' ελέγχει το index page.Σε μελλοντικές εκδόσεις πρέπει να
 * προστεθούν η λειτουργία της εγράφης στο σύστημα και να τελειώσει η
 * λειτουργεία της εισόδου στο σύστημα.
 */
app.controller('IndexCtrl', ['$scope','$window','$location',function ($location,$scope,$window) {
   /**
	 * Η ακόλουθη συνάρτηση εκτελεί εικονικά την λειτουργεία του logIn απλά
	 * ανοίγει την σελίδα με τα notifications του χρήστη.
	 */
	$scope.logIn=function(){
    	 location.replace('#/notifications');
    	 location.reload();

    }
}]);
/** Ο ακόλουθος controller ελέγχει την όψη error. */
app.controller('errorCtrl', ['$scope', function ($scope) {	
}]);
/**
 * Ο ακόλουθος controller ελέγχει την όψη editProfile. Σε αυτόν τον controller
 * θα προστεθούν όλες η συνάρτησης που αφορούν τες λειτουργείς της επεξεργασίας
 * των προσωπικών στοιχείων του χρήστη.
 */
app.controller('editProfileCtrl', ['$scope', function ($scope) {
}]);
/**
 * Ο ακόλουθος controller ελέγχει την όψη 'notifications'.Κάθε φορά που
 * ενεργοποιείτε εκτελεί http get request στον server ζητώντας των κατάλογο των
 * αλλαγών στα έγραφα των ROOMS που είναι μέλος ο χρήστης με Id=1 και τα
 * παρουσιάζει στον χρήστη. Χρησιμοποιείτε Id χρήστη ίσο με 1 επειδή δεν έχει
 * υλοποιηθεί η λειτουργεία της δημιουργίας χρήστη.
 */
app.controller('notificationsCtrl', ['$scope','$http','$location',function ($scope,$http,$location) {
	$scope.RestQuery = "RestApi/request=/";
    $scope.userId=1;
    $scope.RestQueryBase =$scope.RestQuery +$scope.userId+"/getNotifications";
    $('#loadingBarModal').modal('show'); 
	$http.get($scope.RestQueryBase).success(function(response) {	
		$scope.Data=response.response;
    	$scope.noNotifications=false;

        if( $scope.Data.error.message!="None"){
            if( $scope.Data.error.message=="No new documents were added since your last login"){ 
            	$scope.notificationsMessage=$scope.Data.error.message;
            	$scope.noNotifications=true;
            }else{
                $location.path('/error');
                $location.replace();
            }           
        }
        $scope.documents= $scope.Data.documents;
        $('#loadingBarModal').modal('hide'); 
	 }).error(function(data, status, headers, config) {
	        $('#loadingBarModal').modal('hide'); 
	        $location.path('/error');
	        $location.replace(); 
	 });
	
}]);
/**
 * Ο ακόλουθος controller ελέγχει την όψη 'messages.Σε αυτόν τον controller θα
 * προστεθούν όλες η συνάρτησης που αφορούν τες λειτουργείς της αποστολείς
 * μηνυμάτων μεταξύ των χρηστών της πλατφόρμας.
 */
app.controller('messagesCtrl', ['$scope', function ($scope) {	
}]);
/**
 * *Ο ακόλουθος controller ελέγχει την όψη 'adminPanel.Σε αυτόν τον controller
 * θα προστεθούν όλες η συνάρτησης που αφορούν τες λειτουργείς διαχείρισης των
 * Room.
 */
app.controller('adminPanelCtrl', ['$scope', function ($scope) {	
}]);
/**
 * Ο ακόλουθος controller ελέγχει την όψη 'home'. Κάθε φορά που ενεργοποιείτε
 * εκτελεί http get request στον server ζητώντας των κατάλογόν των περιεχομένων
 * ενός Room που είναι μέλος ο χρήστης με Id=1 και τα παρουσιάζει στον χρήστη.
 * Χρησιμοποιείτε Id χρήστη ίσο με 1 επειδή δεν έχει υλοποιηθεί η λειτουργεία
 * της δημιουργίας χρήστη. Ακόμα περιέχει συνάρτησής για την δημιουργία
 * καινούριου εγγράφου και καινούριου Room.Την ανανέωση κάποιου document και την
 * διαγραφή κάποιου Room.
 */
app.controller('homeCtrl', ['$scope','$http','$routeParams','$location','$http','steam',function ($scope,$http,$routeParams,$location,$http,steam) {
    $('#loadingBarModal').modal('show'); 
	$scope.RestQuery = "RestApi/request=/";
    $scope.userId=1;
    $scope.RestQueryBase =$scope.RestQuery +$scope.userId;
    if($routeParams.paramiters==null){
        $scope.RestQuery=  $scope.RestQuery+  $scope.userId+'/home/';
    }else{
        $scope.RestQuery=  $scope.RestQuery+  $scope.userId+'/home/'+$routeParams.paramiters;
    }
    $http.get($scope.RestQuery).success(function(response) {
        $scope.Data=response.response;
        console.log($scope.Data);
        var path=$routeParams.paramiters;
        $scope.empty=false;
        if( $scope.Data.error.message!="None"){
            if( $scope.Data.error.message=="The room does not have any files"||$scope.Data.error.message=="You are not member of any room"){ 
                $scope.empty=true;
            }else{
                $location.path('/error');
                $location.replace();
            }
        }
        $scope.documents= $scope.Data.documents;
        $scope.rooms= $scope.Data.rooms;
        $scope.isEmpty=((typeof $scope.documents== 'undefined')&&(typeof $scope.rooms== 'undefined'));
        if(path!=null){
            var indexs=[];
            for (i = 0; i < path.length; i++) { 
                if(path.charAt(i)==='/')
                    indexs.push(i);
            }        	        	
            var name=path.split('/');
            var paths=[];
            paths.push({name:'home',url:'/home'});
            var i=0;      
            for (i = 0; i < indexs.length; i++) { 
                paths.push({name:name[i],url:'/home/'+path.substring(0,indexs[i])});
            }
            paths.push({name:name[i],url:'/home/'+path});
            $scope.paths=paths;
            $scope.name=name[i];
            $scope.url='/home/'+path;
        }else{
            $scope.paths=[{name:'home',url:'/home'}];
            $scope.name="home";
            $scope.url='/home';
        }    $('#loadingBarModal').modal('hide'); 
        document.getElementById("file-form").action=$scope.RestQueryBase+"/uploadFile"+$scope.url;
    }).error(function(data, status, headers, config) {
        $('#loadingBarModal').modal('hide'); 
        $location.path('/error');
        $location.replace(); 
    });
    $scope.UpdateFileName="";
    /**
	 * Η ακόλουθη συνάρτηση χρησιμοποιείτε για την παρουσίαση ενός bootstrap3
	 * modal που αφορά την λειτουργεία της ανανέωσης κάποιου εγγράφου.
	 */
    $scope.showUpadateModal=function(name){
        $scope.UpdateFileName=name;
        $('#updateFile').modal('show'); 
    }
    /**
	 * Η ακόλουθη συνάρτηση χρησιμοποιείτε για την παρουσίαση ενός bootstrap3
	 * modal που αφορά την λειτουργεία της δημιουργίας καινούριου εγγράφου στην
	 * πλατφόρμα.
	 */
    $scope.showUploadModal=function(){
        $('#uploadFileModal').modal('show'); 
    }
/**
 * Η ακόλουθη συνάρτηση χρησιμοποιείτε για την παρουσίαση των κουμπιών για
 * upload ,update.Καθώς δεν πρέπει να εμφανίζονται σε επίπεδο rootRoom αφού δεν
 * επιτρέπετε η δημιουργία εγγράφων στο RootRoomσ
 */
    $scope.isHome=function(){
        return ($scope.name=="home");
    }
    /**
	 * Η ακόλουθη συνάρτηση χρησιμοποιείτε για την ανανέωση κάποιου εγγράφου.
	 * Παίρνει τα δεδομένα του εγγράφου από την ανάλογη φόρμα και εκτελεί http
	 * post request στο server για ανανέωση του εγγράφου.
	 */
    $scope.updateFile=function(){
        var request=$scope.RestQueryBase+"/updateFile"+$scope.url+"/"+$scope.UpdateFileName;
        var fd = new FormData();
        var files= document.getElementById('updatefile').files;
        fd.append('file',files[0]);
        $http.post(request, fd, {transformRequest: angular.identity,headers: {'Content-Type': undefined}}).success(function(response){
            if(response.response.error.message=="None"){
                $('#updateFile').modal('hide');
                alert("successfully updated");
            }else{
                $('#updateFile').modal('hide');
                alert(response.response.error.message);}
        }).error(function(){
            alert("Error while uploading");
        });	 
    }
    /**
	 * Η ακόλουθη συνάρτηση χρησιμοποιείτε για την διαγραφή κάποιου Room.
	 * Εκτελεί http delete request στο server για την διαγραφεί του ανάλογου
	 * Room.
	 */
    $scope.deleteRoom=function(url){
        $http.delete($scope.RestQueryBase+"/deleteRoom/"+url).success(function(response){
            location.reload();
            alert("The Room successfully deleted");
        }).error(function(){
            alert("Error while deleting");
        });
    }
    /**
	 * Η ακόλουθη συνάρτηση χρησιμοποιείτε για την δημιουργία κάποιου Room.
	 * Εκτελεί http put request στο server για την δημιουργία καινούριου Room.
	 */
    $scope.createRoom=function(){
        var roomName=$scope.roomName;
            var request=$scope.RestQueryBase+"/createRoom"+$scope.url+"/"+roomName;
            $http.put(request).success(function(response){
                if(response.response.error.message=="None"){
                    $('#createRoomModal').modal('hide');
                    alert("The Room successfully created");
                    location.reload();
                }else{
                    $('#createRoomModal').modal('hide'); 
                    alert(response.response.error.message);}
            }).error(function(){
                alert("Error while uploading");
            });
        
    }
/**
 * Η ακόλουθη συνάρτηση χρησιμοποιείτε για την δημιουργία κάποιου καινούριου
 * εγγράφου. Παίρνει τα δεδομένα του εγγράφου από την ανάλογη φόρμα και εκτελεί
 * http post request στο server για δημιουργία του εγγράφου.
 */
    $scope.createNewFile=function() {
        var request=$scope.RestQueryBase+"/uploadFile"+$scope.url;
        var fd = new FormData();
        var files= document.getElementById('file').files;
        fd.append('file',files[0]);
        $http.post(request, fd, {transformRequest: angular.identity,headers: {'Content-Type': undefined}}).success(function(response){
            if(response.response.error.message=="None"){
                $('#uploadFileModal').modal('hide');
                location.reload();
            }else{
                $('#uploadFileModal').modal('hide');
                alert(response.response.error.message);}
        }).error(function(){
            alert("Error while uploading");
        });
    } 

}]);
/**
 * Ο ακόλουθος controller ελέγχει το navigation bar.Περιέχει συναρτήσεις ορισμού
 * της κλάσεις κάποιου menu element και λειτουργείας της αναγνωρίσεις εάν ο
 * χρήστης είναι συνδεδεμένος στην πλατφόρμα.
 */
app.controller('navController', ['$scope', '$location','steam',function ($scope,$location,steam) {       
    /**
	 * *Η ακόλουθη συνάρτηση ορίζει την κλάση ενός αντικείμενου του μενού σε
	 * active εάν ο χρηστής βρίσκετε στην ανάλογη σελίδα.
	 */
	$scope.setClass = function(path) {    
        if ($location.path().substr(0, path.length) == path) {
            return "active"
        }else{
            return ""
        }
    }
/**
 * Η ακόλουθη συνάρτηση επιστρέφει true εάν ο χρήστης δεν είναι συνδεδεμένος
 * στην πλατφόρμα .Χρησιμοποιείτε στην παρουσίαση και την αποκρύψει ενός
 * αντικειμένου του μενού.
 */
    $scope.isLogOut=function(){
    	var mylocation=$location.path();
    	return ( mylocation=== "/"||mylocation=== "/index"||mylocation=== "/index.html");         
    }    
}]);