describe("Codes Matcher test", function() {

    var matcher;

    var codesToMatch = {
        'koodistoUri': 'koodistouri',
        'latestKoodistoVersio': {
        'metadata': [{ 'kieli': 'FI', 'nimi' : 'Ääkköinen'},
                     {'kieli': 'SV', 'nimi': 'with spåces'},        
                     {'kieli': 'EN', 'nimi': 'In cAPs'}        
                     ]
        }
    }

    beforeEach(module("koodisto"))

    beforeEach(inject(function(CodesMatcher) {
    matcher = CodesMatcher;
    }))

    it("returns true if filter is undefined", function() {
    expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, null)).toBeTruthy();
    })

    it("returns false if filter's length is below 2", function() {
    expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "a")).toBeFalsy();
    })

    it("codes does not match filter", function() {
    expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "koodistourr")).toBeFalsy();
    })

    it("koodiUri matches filter", function() {
    expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "kood")).toBeTruthy();
    })

    it("finnish name with scandic letters smatches filter", function() {
    expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "äkkö")).toBeTruthy();
    })

    it("swedish name with scandic letters and spaces smatches filter", function() {
    expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "h spåce")).toBeTruthy();
    })
    
    it("english name with capital letters and spaces smatches filter", function() {
    expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "nca")).toBeTruthy();
    expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "incaps")).toBeTruthy();
    expect(matcher.nameOrTunnusMatchesSearch(codesToMatch, "INCAPS")).toBeTruthy();
    })


})