package com.shopifydemodemo.app.shopifyqueries

import com.shopify.buy3.Storefront
import com.shopify.buy3.Storefront.*
import com.shopify.graphql.support.ID

object MutationQuery {
    fun getLoginDetails(username: String, password: String): Storefront.MutationQuery {
        val input = Storefront.CustomerAccessTokenCreateInput(username, password)
        return Storefront.mutation { mutation ->
            mutation
                .customerAccessTokenCreate(
                    input
                ) { query ->
                    query
                        .customerAccessToken { customerAccessToken ->
                            customerAccessToken
                                .accessToken()
                                .expiresAt()
                        }
                        .customerUserErrors { cue -> cue.field().message() }
                }
        }
    }

    fun createaccount(
        firstname: String,
        lastname: String,
        email: String,
        password: String
    ): Storefront.MutationQuery {
        val customer = Storefront.CustomerCreateInput(email, password)
        customer.firstName = firstname
        customer.lastName = lastname
        return Storefront.mutation { root ->
            root
                .customerCreate(
                    customer
                ) { customerquery ->
                    customerquery
                        .customer { customerdata ->
                            customerdata
                                .firstName()
                                .lastName()
                                .email()
                                .id()
                        }
                        .customerUserErrors { cue -> cue.field().message() }
                }
        }
    }

    fun renewToken(token: String?): Storefront.MutationQuery {
        return Storefront.mutation { root ->
            root
                .customerAccessTokenRenew(
                    token
                ) { access ->
                    access
                        .customerAccessToken { r ->
                            r
                                .accessToken()
                                .expiresAt()
                        }
                        .userErrors { error ->
                            error
                                .message()
                                .field()
                        }
                }
        }
    }

    fun updateCustomer(
        input: Storefront.CustomerUpdateInput,
        token: String
    ): Storefront.MutationQuery {
        return Storefront.mutation { root ->
            root
                .customerUpdate(
                    token, input
                ) { customer ->
                    customer
                        .customer { c ->
                            c
                                .firstName()
                                .lastName()
                                .email()
                                .id()
                        }
                        .customerAccessToken { access ->
                            access
                                .expiresAt()
                                .accessToken()
                        }
                        .customerUserErrors { cue -> cue.message().field() }
                }
        }
    }

    fun recoverCustomer(email: String): Storefront.MutationQuery {
        return Storefront.mutation { root ->
            root
                .customerRecover(
                    email
                ) { recover -> recover.customerUserErrors { cue -> cue.field().message() } }
        }
    }

    fun deleteCustomerAddress(token: String?, address_id: ID?): Storefront.MutationQuery {
        return Storefront.mutation { root ->
            root
                .customerAddressDelete(
                    address_id, token
                ) { customer ->
                    customer
                        .customerUserErrors { cue -> cue.field().message() }
                }
        }
    }

    fun addAddress(
        input: Storefront.MailingAddressInput?,
        token: String?
    ): Storefront.MutationQuery {
        return Storefront.mutation { root ->
            root.customerAddressCreate(
                token,
                input
            ) { address -> address.customerUserErrors { cue -> cue.field().message() } }
        }
    }

    fun updateAddress(
        input: Storefront.MailingAddressInput?,
        token: String?,
        address_id: ID?
    ): Storefront.MutationQuery {
        return Storefront.mutation { root ->
            root.customerAddressUpdate(token, address_id, input) { address ->
                address
                    .customerAddress { caddress ->
                        caddress.firstName().lastName().address1().address2().city().country()
                            .province().phone().zip()
                    }
                    .customerUserErrors { cue -> cue.field().message() }
            }
        }
    }

    fun checkoutCustomerAssociateV2(
        checkoutId: ID?,
        customerAccessToken: String?
    ): Storefront.MutationQuery {
        return mutation { mutation: Storefront.MutationQuery ->
            mutation
                .checkoutCustomerAssociateV2(
                    checkoutId, customerAccessToken
                ) { query: CheckoutCustomerAssociateV2PayloadQuery ->
                    query
                        .checkout { check -> check.webUrl() }
                        .userErrors { userError: UserErrorQuery ->
                            userError
                                .field()
                                .message()
                        }
                }
        }
    }

    fun checkoutDiscountCodeApply(
        checkoutId: ID?,
        discount_code: String?
    ): Storefront.MutationQuery {
        return mutation { mutation: Storefront.MutationQuery ->
            mutation.checkoutDiscountCodeApplyV2(discount_code, checkoutId)
            { query: CheckoutDiscountCodeApplyV2PayloadQuery ->
                query
                    .checkout { check ->
                        check.webUrl()
                            .paymentDueV2({ pd -> pd.amount().currencyCode() })
                            .subtotalPriceV2({ st -> st.currencyCode().amount() })
                            .taxesIncluded()
                            .taxExempt()
                            .totalTaxV2({ tt -> tt.amount().currencyCode() })
                            .totalPriceV2({ tp -> tp.currencyCode().amount() })
                    }
                    .userErrors { userError: UserErrorQuery ->
                        userError
                            .field()
                            .message()
                    }
            }

        }
    }

    fun checkoutDiscountCodeRemove(checkoutId: ID?): Storefront.MutationQuery {
        return mutation { mutation: Storefront.MutationQuery ->
            mutation.checkoutDiscountCodeRemove(checkoutId)
            { query: CheckoutDiscountCodeRemovePayloadQuery ->
                query
                    .checkout { check ->
                        check.webUrl()
                            .paymentDueV2({ pd -> pd.amount().currencyCode() })
                            .subtotalPriceV2({ st -> st.currencyCode().amount() })
                            .taxesIncluded()
                            .taxExempt()
                            .totalTaxV2({ tt -> tt.amount().currencyCode() })
                            .totalPriceV2({ tp -> tp.currencyCode().amount() })
                    }
                    .userErrors { userError: UserErrorQuery ->
                        userError
                            .field()
                            .message()
                    }
            }
        }
    }


    fun checkoutGiftCardsAppend(
        checkoutId: ID?,
        gift_card: List<String>?
    ): Storefront.MutationQuery {
        return mutation { mutation: Storefront.MutationQuery ->
            mutation.checkoutGiftCardsAppend(gift_card, checkoutId)
            { query: CheckoutGiftCardsAppendPayloadQuery ->
                query
                    .checkout { check ->
                        check.webUrl()
                            .paymentDueV2({ pd -> pd.amount().currencyCode() })
                            .subtotalPriceV2({ st -> st.currencyCode().amount() })
                            .taxesIncluded()
                            .taxExempt()
                            .totalTaxV2({ tt -> tt.amount().currencyCode() })
                            .totalPriceV2({ tp -> tp.currencyCode().amount() })
                            .appliedGiftCards { gc ->
                                gc.amountUsedV2(
                                    { a ->
                                        a.amount()
                                            .currencyCode()
                                    }
                                )
                            }
                    }
                    .userErrors { userError: UserErrorQuery ->
                        userError
                            .field()
                            .message()
                    }
            }
        }
    }


    fun checkoutGiftCardsRemove(appliedGiftCardId: ID?, checkoutId: ID?): Storefront.MutationQuery {
        return mutation { mutation: Storefront.MutationQuery ->
            mutation.checkoutGiftCardRemoveV2(appliedGiftCardId, checkoutId)
            { query: CheckoutGiftCardRemoveV2PayloadQuery ->
                query
                    .checkout { check ->
                        check.webUrl()
                            .paymentDueV2({ pd -> pd.amount().currencyCode() })
                            .subtotalPriceV2({ st -> st.currencyCode().amount() })
                            .taxesIncluded()
                            .taxExempt()
                            .totalTaxV2({ tt -> tt.amount().currencyCode() })
                            .totalPriceV2({ tp -> tp.currencyCode().amount() })
                            .appliedGiftCards { gc ->
                                gc.amountUsedV2(
                                    { a ->
                                        a.amount()
                                            .currencyCode()
                                    }
                                )
                            }
                    }
                    .userErrors { userError: UserErrorQuery ->
                        userError
                            .field()
                            .message()
                    }
            }
        }
    }

    fun multipass(multipassToken: String?): Storefront.MutationQuery {

        return Storefront.mutation { root ->
            root.customerAccessTokenCreateWithMultipass(multipassToken)
            { query: CustomerAccessTokenCreateWithMultipassPayloadQuery ->
                query.customerAccessToken { c ->
                    c.accessToken()
                        .expiresAt()

                }
                    .customerUserErrors { e ->
                        e.message()
                            .field()
                    }

            }

        }
    }


}

