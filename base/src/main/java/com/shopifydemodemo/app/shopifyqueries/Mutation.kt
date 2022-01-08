package com.shopifydemodemo.app.shopifyqueries

import com.shopify.buy3.Storefront
import com.shopify.graphql.support.ID

object Mutation {
    fun createCheckout(
        inputs: Storefront.CheckoutCreateInput,
        list_currency: List<Storefront.CurrencyCode>
    ): Storefront.MutationQuery {
        var createcheckoutmutation: Storefront.MutationQuery? = null
        createcheckoutmutation = Storefront.mutation { root ->
            root
                .checkoutCreate(
                    inputs
                ) { checkoutquery ->
                    checkoutquery
                        .checkout { check ->
                            check
                                .webUrl()
                                .paymentDueV2({ pd -> pd.amount().currencyCode() })
                                .subtotalPriceV2({ st -> st.currencyCode().amount() })
                                .taxesIncluded()
                                .taxExempt()
                                .totalTaxV2({ tt -> tt.amount().currencyCode() })
                                .totalPriceV2({ tp -> tp.currencyCode().amount() })
                                .lineItems({ it.first(50) }, { args ->
                                    args.edges { egs ->
                                        egs.cursor()
                                            .node { linenode ->
                                                linenode
                                                    .title()
                                                    .quantity()
                                                    .variant({ linevariant ->
                                                        linevariant
                                                            .product({ p -> p })
                                                            .availableForSale()
                                                            .price()
                                                            .currentlyNotInStock()
                                                            .quantityAvailable()
                                                            .presentmentPrices({ arg ->
                                                                arg.first(25).presentmentCurrencies(
                                                                    list_currency
                                                                )
                                                            },
                                                                { price ->
                                                                    price.edges({ e ->
                                                                        e.cursor().node({ n ->
                                                                            n.price({ p ->
                                                                                p.amount()
                                                                                    .currencyCode()
                                                                            })
                                                                                .compareAtPrice({ cp ->
                                                                                    cp.amount()
                                                                                        .currencyCode()
                                                                                })
                                                                        })
                                                                    })
                                                                }
                                                            )
                                                            .priceV2({ p ->
                                                                p.currencyCode().amount()
                                                                    .currencyCode()
                                                            })
                                                            .compareAtPriceV2({ c ->
                                                                c.amount().amount().currencyCode()
                                                            })
                                                            .image({ img ->
                                                                img
                                                                    .transformedSrc({ trans ->
                                                                        trans
                                                                            .maxHeight(200)
                                                                            .maxWidth(200)
                                                                    }
                                                                    )
                                                                    .originalSrc()
                                                            }
                                                            )
                                                            .selectedOptions({ select ->
                                                                select
                                                                    .name()
                                                                    .value()

                                                            }
                                                            )

                                                    }
                                                    )
                                            }
                                    }
                                })
                        }
                        .checkoutUserErrors { checkerror -> checkerror.field().message().code() }
                }
        }
        return createcheckoutmutation
    }

    fun checkoutWithGpay(
        checkoutID: ID,
        input: Storefront.TokenizedPaymentInputV3
    ): Storefront.MutationQuery {
        return Storefront.mutation { it ->
            it.checkoutCompleteWithTokenizedPaymentV3(checkoutID, input) { _queryBuilder ->
                _queryBuilder.payment { paymentquery ->
                    paymentquery.ready().errorMessage()
                }
                    .checkout { checkoutQuery ->
                        checkoutQuery.ready()
                    }
                    .checkoutUserErrors { userErrorQuery ->
                        userErrorQuery.field().code().message()
                    }
            }
        }
    }
}
