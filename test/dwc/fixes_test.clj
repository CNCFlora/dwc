(ns dwc.fixes-test
  (:use dwc.fixes
        midje.sweet))

(fact "Coordinates to decimal"
  (coord2decimal "38 53 55N") => 38.89861297607422
  (coord2decimal "38d 53 55 N") => 38.89861297607422
  (coord2decimal "38d 53' 55'' S") => -38.89861297607422
  (coord2decimal "77 2 16W") => -77.03778076171875)

(fact "It can fix coords systems"
  (fix-coords {:latitude "38d 53 55N" :longitude "77 2' 16'' W"})
      => {:latitude "38d 53 55N" :longitude "77 2' 16'' W" 
          :decimalLatitude 38.89861297607422 :decimalLongitude -77.03778076171875}
  (fix-coords {:latitude "38d 53 55N"})
      => {:latitude "38d 53 55N" }
  (fix-coords {:decimalLatitude 38.89861297607422 :decimalLongitude -77.03778076171875})
      => {:decimalLatitude 38.89861297607422 :decimalLongitude -77.03778076171875})

(fact "Fixes occurrenceID"
  (fix-id {:occurrenceID "123" :id "321"})
    => {:occurrenceID "123" :id "321"}
  (fix-id {:id "321"})
    => {:occurrenceID "321" :id "321"}
  (fix-id {:institutionCode "inst" :collectionCode "col" :catalogNumber "num"} )
    => {:institutionCode "inst" :collectionCode "col" :catalogNumber "num" :occurrenceID "urn:occurrence:inst:col:num"}
  )

(fact "apply many fixes"
  (-fix-> {:id "123"})
      => {:occurrenceID "123" :id "123"}
  (-fix-> [ {:id "123"} {:id "321"}])
      => [{:occurrenceID "123" :id "123"} {:occurrenceID "321" :id "321"}]
  )
