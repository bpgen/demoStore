Feature: Store activities
 # Test for Owner's All Type of Activity
  Scenario Outline: Store Owner's all activities
    When store owner creates new products with "<productCode>","<desc>","<price>","<currency>","<quantity>","<discountDeal>","<bundleDeal>" with response code 201
    Then store owner retrieves product code "<productCode>"
    And store owner update product's price "<updatePrice>" or desc "<updateDesc>" for code "<productCode>"
    And store owner delete product with "<productCode>" and current product listing size is 0
    Examples: first table
      | productCode | desc            | price | currency |quantity | discountDeal                  | bundleDeal                           | updatePrice | updateDesc    |
      | A1         | 10kg Wheat pkt  | 6.90  |  USD     |7        | buy 2 get 50% off the third   | buy 1 pkt, get 200g ginger for free  | 7.54        | 7kg Wheat pkt |
      | AA2        | 7kg Rice pkt    | 7.52  |  USD     |18       | buy 1 get 50% off the second  | buy 1 pkt, get 200g garlic for free  | 9.54        | 5kg Rice pkt  |
      | B3         | 5kg Potato pkt  | 4.52  |  USD     |11       | buy 1 get 50% off the second  | buy 1 pkt, get 200g garlic for free  | 8.54        | 4kg Potato pkt|
      | D4         | 7kg Rice pkt    | 7.52  |  USD     |9        | buy 1 get 50% off the second  | buy 1 pkt, get 100g garlic for free  | 7.51        | 2kg Sugar pkt |

# SETUP few products before Buyer can start checkout - START
  Scenario Outline: Store Owner's activity - Setup few Products
    When store owner creates new products with "<productCode>","<desc>","<price>","<currency>","<quantity>","<discountDeal>","<bundleDeal>" with response code 201
    Examples:
      | productCode | desc            | price | currency |quantity | discountDeal                  | bundleDeal   |
      | C11        | 10kg Wheat pkt  | 6.90  |  USD     |7        | buy 1 get 25% off the second  |              |
  Scenario Outline: Store Owner's activity - Setup few Products
    When store owner creates new products with "<productCode>","<desc>","<price>","<currency>","<quantity>","<discountDeal>","<bundleDeal>" with response code 201
    Examples:
      | productCode | desc            | price | currency |quantity | discountDeal  | bundleDeal                             |
      | B12        | 7kg Rice pkt     | 7.52  |  USD     |18       | no discount   | buy any pkt, get 200g Ginger for free  |
  Scenario Outline: Store Owner's activity - Setup few Products
    When store owner creates new products with "<productCode>","<desc>","<price>","<currency>","<quantity>","<discountDeal>","<bundleDeal>" with response code 201
    Examples:
      | productCode | desc            | price | currency |quantity | discountDeal                  | bundleDeal                             |
      | A13         | 5kg Potato pkt  | 4.52  |  USD     |11       | buy 2 get 50% off the third   | buy any pkt, get 200g Garlic for free  |
# SETUP few products before Buyer can start checkout - END

# Test for BUYER's checkout Activity
   # C11 product added with discount
  Scenario Outline: Buyer's all activities
    When buyer with "<userId>" checkout "<quantity>" "<productCode>" with response code 201
    Then buyer with "<userId>" see total price "<totalPrice>" with "<bundleDeal>"
    Examples:
      | userId| productCode | quantity | totalPrice  | bundleDeal |
      | 1     |    C11      | 6        | 36.23       |            |

     # Can not add further 6 number of C11 product since stock is only 7 product
  Scenario Outline: Buyer's all activities
    When buyer with "<userId>" checkout "<quantity>" "<productCode>" with response code 400
    Examples:
      | userId| productCode | quantity |
      | 1     |    C11      | 6        |

   # no discount on B12 but got bundle deal
  Scenario Outline: Buyer's all activities
    When buyer with "<userId>" checkout "<quantity>" "<productCode>" with response code 201
    Then buyer with "<userId>" see total price "<totalPrice>" with "<bundleDeal>"
    Examples:
      | userId| productCode | quantity | totalPrice  | bundleDeal                            |
      | 1     |    B12      | 1        | 43.75       | buy any pkt, get 200g Ginger for free,|

     # discount on A13
  Scenario Outline: Buyer's all activities
    When buyer with "<userId>" checkout "<quantity>" "<productCode>" with response code 201
    Then buyer with "<userId>" see total price "<totalPrice>" with "<bundleDeal>"
    Examples:
      | userId| productCode | quantity | totalPrice  | bundleDeal                                                                  |
      | 1     |    A13      | 3        | 55.05       | buy any pkt, get 200g Ginger for free,buy any pkt, get 200g Garlic for free, |

    # Due to reduction of quantity, discount on A13 will be removed
  Scenario Outline: Buyer's all activities
    When buyer with "<userId>" checkout reduce "<quantity>" "<productCode>" with response code 202
    Then buyer with "<userId>" see total price "<totalPrice>" with "<bundleDeal>"
    Examples:
      | userId| productCode | quantity | totalPrice  | bundleDeal                                                                  |
      | 1     |    A13      | 1        | 52.79       | buy any pkt, get 200g Ginger for free,buy any pkt, get 200g Garlic for free, |

## Test for BUYER's checkout Activity - see the final prices and bundle deal detail