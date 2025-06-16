describe('Order flow', () => {
  it('places an order', () => {
    cy.visit('/')
    cy.contains('Add to cart').first().click()
    cy.get('a[href="/cart"]').click()
    cy.contains('Checkout').click()
    cy.get('input[name="customerName"]').type('Test User')
    cy.contains('Submit Order').click()
  })
})
