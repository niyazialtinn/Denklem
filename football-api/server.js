const express = require("express");

const app = express();

const PORT = process.env.PORT || 10000;
const API_KEY = process.env.API_FOOTBALL_KEY;

app.get("/", (req, res) => {
    res.json({
        app: "KuponHesaplayici Football API",
        status: "online"
    });
});

app.get("/health", (req, res) => {
    res.json({
        status: "ok"
    });
});

app.get("/live", async (req, res) => {
    try {
        if (!API_KEY) {
            return res.status(500).json({
                error: "API_FOOTBALL_KEY tanımlı değil."
            });
        }

        const response = await fetch(
            "https://v3.football.api-sports.io/fixtures?live=all",
            {
                method: "GET",
                headers: {
                    "x-apisports-key": API_KEY
                }
            }
        );

        const data = await response.json();

        res.json(data);

    } catch (error) {
        console.error(error);

        res.status(500).json({
            error: "API-Football bağlantısında hata oluştu."
        });
    }
});

app.get("/today", async (req, res) => {
    try {
        if (!API_KEY) {
            return res.status(500).json({
                error: "API_FOOTBALL_KEY tanımlı değil."
            });
        }

        const today = new Date().toISOString().split("T")[0];

        const response = await fetch(
            `https://v3.football.api-sports.io/fixtures?date=${today}&timezone=Europe/Istanbul`,
            {
                method: "GET",
                headers: {
                    "x-apisports-key": API_KEY
                }
            }
        );

        const data = await response.json();

        res.json(data);

    } catch (error) {
        console.error(error);

        res.status(500).json({
            error: "API-Football bağlantısında hata oluştu."
        });
    }
});

app.listen(PORT, "0.0.0.0", () => {
    console.log(`KuponHesaplayici API ${PORT} portunda çalışıyor.`);
});
