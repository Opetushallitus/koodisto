"use strict";
app.factory('OrganisaatioTreeModel', function(Organizations, OrganizationChildrenByOid, OrganizationByOid) {

    return (function() {
        var instance = {};
        instance.model = {};
        instance.searchStr = "";

        var oph = {
            "oid" : "1.2.246.562.10.00000000001",
            "parentOidPath" : "1.2.246.562.10.00000000001",
            "nimi" : {
                "fi" : "OPH",
                "sv" : "OPH",
                "en" : "OPH"
            },
            "children": []
        };
        
        instance.init = function(organizations) {                       
            instance.model = {};
            instance.userBelongsToOph = jQuery.inArray("1.2.246.562.10.00000000001", organizations) != -1; //TODO: move oph oid into auth.js?
            instance.model.organisaatiot = [];
            instance.model.numHits = 0;
            
            if (instance.userBelongsToOph) {
        	OrganizationByOid.get({oid: "1.2.246.562.10.00000000001"}, function(result) {
        	    instance.model.organisaatiot.push(result);
        	    instance.model.numHits += 1;
        	});
            } else {
        	organizations.forEach(function(organization) {
        	    OrganizationChildrenByOid.get({oid: organization}, function(result) { 
        		result.organisaatiot.forEach(function(org) {
        		    instance.model.organisaatiot.push(org);
        		});
        		instance.model.numHits += result.numHits;
        	    });
        	});
            }
        };
        
        instance.resetSearch = function() {
            if (instance.userBelongsToOph) {
        	OrganizationByOid.get({oid: "1.2.246.562.10.00000000001"}, function(result) {
        	    instance.model.organisaatiot.push(result);
        	    instance.model.numHits += 1;
        	});
            } else if (instance.model.originalOrganizations){
        	instance.model.organisaatiot =  instance.model.originalOrganizations;
            }
        }

        instance.search = function(searchStr) {
            
            var matchingOrgs = new Array();
            
            function matchesSearch(organization) {
        	function matchesTranslation(organization, language) {
        	    return organization.nimi[language] && organization.nimi[language].toLowerCase().indexOf(searchStr) > -1;
        	}
        	return matchesTranslation(organization, 'fi') || matchesTranslation(organization, 'sv') || matchesTranslation(organization, 'en');
            }
            
            function recursivelyAddMatchingOrganizations(organization) { 
        	if (matchesSearch(organization)) {
        	    matchingOrgs.push(organization);
        	} else {
        	    organization.children.forEach(function(child) {        	    
        		recursivelyAddMatchingOrganizations(child);
        	    });
        	}
            }
            
            if (instance.userBelongsToOph) {
        	Organizations.get({"searchStr": searchStr}, function(result){
        	    instance.model = result;
        	});
            } else {
        	searchStr = searchStr.toLowerCase();
        	
        	if (!instance.model.originalOrganizations) {
        	    instance.model.originalOrganizations = instance.model.organisaatiot;
        	}

        	instance.model.originalOrganizations.forEach(function(organization) {
        	    recursivelyAddMatchingOrganizations(organization);
        	});

        	instance.model.organisaatiot = matchingOrgs;
        	instance.model.numHits = matchingOrgs.length;
            }
            
            if(instance.model.organisaatiot.length < 4) {
        	instance.model.organisaatiot.forEach(function(data){
        	    instance.openChildren(data);
        	});
            }
        }

        instance.openChildren = function(data) {
            data.open = !data.open;
            if(data.open) {

                var iter = function(children){
                    if(children) {
                        children.forEach(function(child){
                            child.open = true;
                            iter(child.children);
                        });
                    }
                }

                iter(data.children);
            }
        };

        return instance;
    })();

});

function OrganisaatioTreeController($scope, AuthService, OrganisaatioTreeModel) {
    $scope.orgTree = OrganisaatioTreeModel;
    if(!OrganisaatioTreeModel.instance) {
	AuthService.getOrganizations(serviceName).then(function(organizations) {
	    OrganisaatioTreeModel.init(organizations);
	});
    }
    $scope.$watch('orgTree.searchStr', function() {
        if($scope.orgTree.searchStr.length > 2) {
            OrganisaatioTreeModel.search($scope.orgTree.searchStr);
        } else if ($scope.orgTree.searchStr.length < 1){
            OrganisaatioTreeModel.resetSearch();
        }            
    });

    function debounce(fn, delay) {
        var timer = null;
        return function () {
            var context = this, args = arguments;
            clearTimeout(timer);
            timer = setTimeout(function () {
                fn.apply(context, args);
            }, delay);
        };
    }

    $scope.openChildren = function(data) {
        OrganisaatioTreeModel.openChildren(data);
    }

    $scope.clear = function(){
        OrganisaatioTreeModel.searchStr = '';
        OrganisaatioTreeModel.resetSearch();
    }


}


var ModalInstanceCtrl = function ($scope, $modalInstance, OrganisaatioTreeModel) {

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };


    $scope.organisaatioSelector = function(data) {
        $modalInstance.close(data);
    };
};