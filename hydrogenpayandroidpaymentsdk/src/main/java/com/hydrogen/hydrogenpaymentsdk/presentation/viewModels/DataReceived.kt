package com.hydrogen.hydrogenpaymentsdk.presentation.viewModels

/*

{
    "responseCode": "0000",
    "responseDescription": "Transaction successful",
    "transactionReference": "503059109999_73289ff0aa",
    "channelTransactionReference": null,
    "amount": 10.00,     // This is the Total Amount Due
    "remittanceAmount": 10.00,  // This is total amount Paid
    "customerName": "Dev Test",
    "bank": "Access Bank",
    "status": "COMPLETED",
    "transactionStatus": "COMPLETED",
    "submitTimeUtc": "01/31/2025 07:19:04", // Date & Time transaction was initiated
    "reconciliationId": "66f30000-d3db-9220-bb53-08dd41bf297a",
    "clientReferenceInformation": "503059109999_73289ff0aa",
    "accountName": "HYDROGENPAY",
    "accountNo": "1811368473",
    "maskedPan": null,
    "cardExpiry": null,
    "errors": null,
    "transactionId": "66f30000-d3db-9220-bb53-08dd41bf297a",
    "completedTimeUtc": "Jan 31st 2025 | 07:21am", // Time Payment was Received
    "canRetry": true,
    "callBackUrl": "https://hydrogenpay.com?TransactionRef=503059109999_73289ff0aa",
    "processorResponse": null,
    "processorTransactionId": null,
    "recurringCardToken": null,
    "email": null,
    "expMonth": null,
    "expYear": null
}

ENDPOINT
https://api.hydrogenpay.com/bepay/api/v1/Payment/purchase

PAYLOAD
{
  "transactionId": "51790000-240f-6a99-41b5-08dd45d468fa",
  "providerId": "38214567",
  "callbackURL": "https://payment.hydrogenpay.com/callback-redirect?transactionId=51790000-240f-6a99-41b5-08dd45d468fa",
  "ipAddress": "105.119.11.214",
  "currency": "ngn"
}

RESPONSE
{
    "statusCode": "90000",
    "message": null,
    "data": "SpxpXlItF3S+7FTUVfdnyJpeQ287//LsLIt1/GZxLMpCpnBWWgmhjkC8bEPGHvztBMnbl5G01yAfZMAk1bLIHPgaO88O6NEGrbVlhK2IcrvfOlqh+q0eAMUQ1B975lwl0Yit/fvo4rDf1kTuhaJq6E+CgBbsCqXb6ziXUtc3/LyB+ZVKd5ip30hEkDbk8fHlvIKL9J/Yh1SoucC6vnOJnIW3Rp59t2X7g4e6oli17RGj6YTXaiQLmdbYWA+tgfCxU4bcQGk5zChmHOjl4pc96gWXh4qCzAMeo2sYF7xEKrNlQvBcMTCtkoNU5xH1rTqJnBZD+b4zv5u+orhuWPT3T9BW2G2nmeEZsOfU7YPjTkIJQV6HznKfgw3alPH95A4w0m26tMT/Lpm5XZqW5LzElC/RdO5+o23MUDwH5Qhk7yUqZ601I0ZnI076vfUjcMcTtd0Ji2NgsmV8/WAC/NVQMfXvZ/ZrEDYSxdfuRXGiKAHWDBMGf1TiCu+Mtj1y0bz6P3eyIczt6PAR0xIaBAdMc9EjtAbWWra5QsN7deTPt+VH8KuZY+UtQpbwgaU/hETYNEeB747ChWizvt7XuLNBTVvNWh5Bm9q59JSUmACV1Rj0zyRkIWebzAxEBTukPCkYolOE3DhLMG+runwYNBjh1haZUd9E9OSTD4eahMkdI6NYL8eDnRKUoeJa0C64qvQS2k+WNuPY+dWSUDO2cTga1Bvw9QP6hrYcBIQwa6h2AdV+mwM1goVs+6dMkdBLSdloaHEO59WNwGnYhmR1fA+oVoTlhd8wmITxQrBM40U7qy0jMAS6TVIsvZIBLUsqpcekFsicY2sgNmer7YgPPZdLSsh8bAxvwF615jDdBNqQn2dAvMoY+gofN+2Vwu2kECl1wj+ZjaK6R1buqarCfGIYjPCbVk3JgQJahWA/XM1YXJApgR3e8Sh46J0awayKS4PZabZ+QQWofAFT2PKVE4Ol0vSBV2A4BefsnIDoFYNZ2xLZ88Ju6tfbHwkYdMatvmmMRhTNsfvXJWI3WRfFthYvMw=="
}


















 **/