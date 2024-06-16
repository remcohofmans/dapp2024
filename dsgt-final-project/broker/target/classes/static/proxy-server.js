import fetch from 'node-fetch';

import express from 'express';
const app = express();
const PORT = 9090;

app.use(express.json());

app.post('/fetch-liquors', async (req, res) => {
    // Handle SOAP request and response fetching here
    // Example SOAP request setup
    const soapRequest = `
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                          xmlns:gs="http://liquormenu.io/gt/webservice">
            <soapenv:Header/>
            <soapenv:Body>
                <gs:getLiquorCardRequest/>
            </soapenv:Body>
        </soapenv:Envelope>
    `;

    try {
        const response = await fetch("http://dappvm.eastus.cloudapp.azure.com:12000/ws/liquors.wsdl", {
            method: "POST",
            headers: {
                "Content-Type": "text/xml",
                "SOAPAction": "http://liquormenu.io/gt/webservice/getLiquorCardRequest"
            },
            body: soapRequest
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const responseText = await response.text();
        // Process SOAP response as needed

        // Assuming parsing logic here...

        // Example response handling
        res.json({ message: 'Data fetched successfully', data: parsedData });
    } catch (error) {
        console.error("Error fetching liquors:", error);
        res.status(500).json({ error: "Failed to fetch liquors" });
    }
});

app.listen(PORT, () => {
    console.log(`Proxy server running on port ${PORT}`);
});
