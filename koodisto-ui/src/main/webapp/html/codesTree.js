import {getLanguageSpecificValueOrValidValue} from "./app";

//domain .. this is both, service & domain layer
export class Treemodel {
    constructor($resource, RootCodes, MyRoles, codesMatcher) {
        "ngInject";
        this.$resource = $resource;
        this.RootCodes = RootCodes;
        this.MyRoles = MyRoles;
        this.codesMatcher = codesMatcher;

        // keep model to yourself
        this.model = {
            name: "ROOT",
            codeList: [],
            myRoleList: []
        };

        this.filter = '';
        this.search = {
            codesfound: 0
        };

        this.refresh();
    }

    // and return interface for manipulating the model
    isVisibleNode(data) {
        return (this.codesMatcher.nameOrTunnusMatchesSearch(data, this.filter.name)) && (!this.filter.own || this.filter.own && this.isOwnedNode(data))
            && (this.filter.passivated || !this.filter.passivated && !this.isPassivatedNode(data))
            && (this.filter.planned || !this.filter.planned && !this.isPlannedNode(data));
    }
    
    isOwnedNode(data) {
        /*
         * MyRoles.get({}, function (result) { model.myRoleList = result; });
         */
    }
    
    isPassivatedNode(data) {
        var today = new Date();
        var endDate = Date.parse(data.latestKoodistoVersio.voimassaLoppuPvm);
        return (!isNaN(endDate) && endDate < today);
    }
    
    isPlannedNode(data) {
        var today = new Date();
        var startDate = Date.parse(data.latestKoodistoVersio.voimassaAlkuPvm);
        return (!isNaN(startDate) && startDate > today);
    }

    getKoodistos(data) {
        return data.koodistos;
    }

    isExpanded(data) {
        return data.isVisible;
    }

    isCollapsed(data) {
        return !this.isExpanded(data);
    }

    getTemplate(data) {
        if (data) {
            if (data.koodistos) {
                return "codesgroup_node.html";
            } else {
                return "codes_leaf.html";
            }
        }
        return "";
    }

    getRootNode() {
        return this.model.codeList;
    }

    expandNode(node) {

    }

    refresh() {
        this.search.codesfound = 0;
        this.model.codeList = [];
        // get initial listing
        this.RootCodes.get({}, (result) => {
            this.model.codeList = result;
            this.update();
        });
    }

    update() {
        this.search.codesfound = 0;
        for (var i = 0; i < this.model.codeList.length; i++) {
            if (this.model.codeList[i].koodistos) {
                for (var j = 0; j < this.model.codeList[i].koodistos.length; j++) {
                    this.model.codeList[i].koodistos[j].isVisible = this.isVisibleNode(this.model.codeList[i].koodistos[j]);
                    this.model.codeList[i].isVisible = this.filter && this.filter.name && this.filter.name.length > 1;
                    if (this.model.codeList[i].koodistos[j].isVisible) {
                        this.search.codesfound++;
                    }
                }
            }
        }
    }

    languageSpecificValue(fieldArray, fieldName, language) {
        return getLanguageSpecificValueOrValidValue(fieldArray, fieldName, language);
    }
}



export class KoodistoTreeController {
    constructor($scope, $resource, $routeParams, treemodel) {
        "ngInject";
        this.$scope = $scope;
        this.$resource = $resource;
        this.$routeParams = $routeParams;
        this.treemodel = treemodel;

        this.predicate = 'koodistoUri';
        this.domain = treemodel;
        if ($routeParams.forceRefresh){
            this.domain.refresh();
        }
    }

    addClass(cssClass, ehto) {
        return ehto ? cssClass : "";
    }

    expandNode(node) {
        node.isVisible = !node.isVisible;
    }
}
