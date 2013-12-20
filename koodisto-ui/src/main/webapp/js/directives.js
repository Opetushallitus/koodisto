/**
 * JQuery Nestable drag'n'drop tree.
 */
app.directive('jqNestable', function ($timeout) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var options = scope.$eval(attrs.jqNestable) || {}
            $(element[0]).nestable({
                group: 1,
                listNodeName: 'ul',
                rootClass: 'gradingformula',
                handleClass: 'icon',
                dragClass: 'gradingformula-dragged',
                placeClass: 'placeholder',
                emptyClass: 'empty',
                listClass: 'list',
                itemClass: 'item',
                maxDepth: 25,
                expandBtnHTML: '',
                collapseBtnHTML: '',
                canDrop: function (pointEl) {
                    return true
                }
            })

            if (typeof options.onChange === 'function') {
                $(element[0]).on('change', function (e, dragged) {
                    // TODO: Voisi innostuessaan tehdä jotain geneerisempää
                    // Haetaan pudotetun elementin scope ja sieltä paikallinen muuttuja 'funktio', asetettu laskentakaavalomake.html:ssä.
                    var draggedFunction = dragged.scope().funktio
                    // Haetaan edellinen parent model
                    var oldParentFunktio = dragged.scope().parent
                    // Haetaan uuden parentin scopesta muuttuja funktio
                    var newParentEl = dragged.parent().parent()
                    var newParentFunktio = newParentEl.scope().funktio
                    var index = dragged.index()
                    options.onChange(draggedFunction, oldParentFunktio, newParentFunktio, index)

                    // Korjataan haamuelementti
                    dragged.remove()

                    $(newParentEl).scope().$apply()
                })
            }
        }
    }
});
app.directive('nestedsortable', function (HakukohdeSiirra) {
    return {
        restrict: 'A',
        require: "^ngController",
        scope: true,
        link: function (scope, element, attrs, ctrl) {
            var options = scope.$eval(attrs.tree) || {}
            $(element[0]).nestedSortable({
                cancel: ".state-disabled",
                //disableNesting: "noNesting",
                handle: 'div',
                toleranceElement: '> div',
                forcePlaceholderSize: true,
                //helper: 'clone',
                helper: 'original',
                listType: 'ol',
                items: 'li',
                opacity: .6,
                placeholder: 'placeholder',
                revert: 250,
                tabSize: 10,
                tolerance: 'pointer',
                toleranceElement: '> div',
                maxLevels: 10,
                isTree: true,
                doNotClear: true,
                //expandOnHover: 700,
                startCollapsed: true,
                //isAllowed: function(e) { return true; },

                update: function (event, ui) {
                    var root = event.target,
                        item = ui.item,
                        parent = item.parent();
                    var hakukohdeOid = item.data('oid');
                    var valintaryhmaOid = parent.data('valintaryhmaoid');
                    if (valintaryhmaOid === undefined) {
                        valintaryhmaOid = "";
                    }
                    var index = item.index();
                    //hyva keino logittaa on pistaa siirrettyyn bg-color!
                    //$(item).attr("style","background-color:red;");

                    //prevent default ei auta! aiheuttaa vaan epasynkkaa!
                    //event.preventDefault();

                    //remove ei auta ellet tee eri threadissa!
                    //$(item).remove();
                    scope.$apply(function () {
                        scope.move(index, hakukohdeOid, valintaryhmaOid, item);

                    });

                }
            });
        }
    }
});


app.directive('uiSortable', function () {
        var options;
        options = {};
        return {
            require: '?ngModel',
            link: function (scope, element, attrs, ngModel) {

                var onStart, onUpdate, opts, _start, _update;
                opts = angular.extend({}, options, scope.$eval(attrs.uiOptions));
                if (ngModel != null) {
                    onStart = function (e, ui) {
                        return ui.item.data('ui-sortable-start', ui.item.index());
                    };
                    onUpdate = function (e, ui) {
                        var end, start;
                        start = ui.item.data('ui-sortable-start');
                        end = ui.item.index();
                        ngModel.$modelValue.splice(end, 0, ngModel.$modelValue.splice(start, 1)[0]);
                        return scope.$apply();
                    };
                    _start = opts.start;
                    opts.start = function (e, ui) {
                        onStart(e, ui);
                        if (typeof _start === "function") {
                            _start(e, ui);
                        }
                        return scope.$apply();
                    };
                    _update = opts.update;
                    opts.update = function (e, ui) {
                        onUpdate(e, ui);
                        if (typeof _update === "function") {
                            _update(e, ui);
                        }
                        return scope.$apply();
                    };
                }
                return element.sortable(opts);
            }
        };
    }
);


app.directive('enter', function () {
    return function (scope, element, attrs) {
        element.bind('mouseover', function () {
            element.addClass(attrs.enter);
        });
    }
});

app.directive('leave', function () {
    return function (scope, element, attrs) {
        element.bind('mouseleave', function () {
            element.removeClass(attrs.leave);
        });
    }
});

app.directive('filterableList', function () {
    return {
        scope: true,
        controller: function ($scope) {
            $scope.selectedId = "";
            var id = "";
            this.setSelectedId = function (newId) {
                $scope.selectedId = newId;
                id = newId;
                $scope.$digest();
            }

            this.removeSelection = function () {
                $scope.selectedId = "";
                id = "";
                $scope.$broadcast('removeSelection');
                $scope.$digest();
            }

            $scope.$watch('id', function () {
                $scope.selectedId = id;
            });

        }
    }
});

app.directive('flOption', function () {
    return {
        require: '^filterableList',

        link: function (scope, iElement, iAttrs, ctrl) {
            var isSelected = false;

            scope.$on('removeSelection', function (event, data) {
                if (isSelected) {
                    iElement.removeClass('selected');
                    isSelected = false;
                }
            });

            iElement.bind('click', function () {
                if (isSelected) {
                    ctrl.removeSelection();
                    isSelected = false;
                } else {
                    ctrl.removeSelection();
                    ctrl.setSelectedId(iAttrs.flOption);
                    iElement.addClass('selected');
                    isSelected = true;
                }
            });

        }
    }
});
