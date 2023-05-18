package price

import "github.com/shopspring/decimal"

type Price struct {
	Amount   decimal.Decimal `json:"Amount"`
	Currency string          `json:"Currency"`
}
