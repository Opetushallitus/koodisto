//domain .. this is both, service & domain layer
app.factory('Treemodel', function($resource, RootCodes, MyRoles, CodesMatcher) {
    // keep model to yourself
    var model = {
        name : "ROOT",
        codeList : [],
        myRoleList : []
    };

    // and return interface for manipulating the model
    var modelInterface = {
        filter : '',
        search : {
            codesfound : 0
        },
        isVisibleNode : function(data) {
            return (CodesMatcher.nameOrTunnusMatchesSearch(data, this.filter.name)) && (!this.filter.own || this.filter.own && this.isOwnedNode(data))
                    && (this.filter.passivated || !this.filter.passivated && !this.isPassivatedNode(data))
                    && (this.filter.planned || !this.filter.planned && !this.isPlannedNode(data));
        },
        isOwnedNode : function(data) {
            /*
             * MyRoles.get({}, function (result) { model.myRoleList = result; });
             */
        },
        isPassivatedNode : function(data) {
            var today = new Date();
            var endDate = Date.parse(data.latestKoodistoVersio.voimassaLoppuPvm);
            return (!isNaN(endDate) && endDate < today);
        },
        isPlannedNode : function(data) {
            var today = new Date();
            var startDate = Date.parse(data.latestKoodistoVersio.voimassaAlkuPvm);
            return (!isNaN(startDate) && startDate > today);
        },
        getKoodistos : function(data) {
            return data.koodistos;
        },
        isExpanded : function(data) {
            return data.isVisible;
        },
        isCollapsed : function(data) {
            return !this.isExpanded(data);
        },
        getTemplate : function(data) {
            if (data) {
                if (data.koodistos) {
                    return "codesgroup_node.html";
                } else {
                    return "codes_leaf.html";
                }
            }
            return "";
        },
        getRootNode : function() {
            return model.codeList;
        },
        expandNode : function(node) {

        },
        refresh : function() {
            modelInterface.search.codesfound = 0;
            model.codeList = [];
            // get initial listing
            RootCodes.get({}, function(result) {
                model.codeList = result;
                modelInterface.update();
            });
        },
        update : function() {
            modelInterface.search.codesfound = 0;
            for (var i = 0; i < model.codeList.length; i++) {
                if (model.codeList[i].koodistos) {
                    for (var j = 0; j < model.codeList[i].koodistos.length; j++) {
                        model.codeList[i].koodistos[j].isVisible = this.isVisibleNode(model.codeList[i].koodistos[j]);
                        model.codeList[i].isVisible = this.filter && this.filter.name && this.filter.name.length > 1;
                        if (model.codeList[i].koodistos[j].isVisible) {
                            modelInterface.search.codesfound++;
                        }
                    }
                }
            }
        },
        languageSpecificValue : function(fieldArray, fieldName, language) {
            return getLanguageSpecificValue(fieldArray, fieldName, language);
        }
    };
    modelInterface.refresh();
    return modelInterface;
});

function KoodistoTreeController($scope, $resource, Treemodel) {
    $scope.predicate = 'koodistoUri';
    $scope.domain = Treemodel;

    $scope.addClass = function(cssClass, ehto) {
        return ehto ? cssClass : "";
    };

    $scope.expandNode = function(node) {
        node.isVisible = !node.isVisible;
    };

}