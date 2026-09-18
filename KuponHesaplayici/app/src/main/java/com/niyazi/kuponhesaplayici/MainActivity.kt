package com.niyazi.kuponhesaplayici

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*

import java.util.Locale

class MainActivity : Activity() {

    private lateinit var kasa: EditText
    private lateinit var container: LinearLayout

    private var count = 3

    private val oddsFields = mutableListOf<EditText>()

    private var resultView: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
    }

    private fun dp(v: Int): Int {
        return (v * resources.displayMetrics.density).toInt()
    }

    private fun text(
        value: String,
        size: Float,
        bold: Boolean = false
    ): TextView {

        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(Color.rgb(30, 30, 30))

            if (bold) {
                typeface = Typeface.DEFAULT_BOLD
            }

            setPadding(
                dp(4),
                dp(6),
                dp(4),
                dp(6)
            )
        }
    }

    private fun buildUi() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL

            setPadding(
                dp(16),
                dp(12),
                dp(16),
                dp(16)
            )

            setBackgroundColor(
                Color.rgb(245, 247, 250)
            )
        }

        val title = text(
            "🎯 Kupon Hesaplayıcı",
            25f,
            true
        )

        title.setTextColor(
            Color.rgb(21, 101, 192)
        )

        title.gravity = Gravity.CENTER

        root.addView(
            title,
            LinearLayout.LayoutParams(
                -1,
                dp(55)
            )
        )

        val subtitle = text(
            "Eşit Geri Dönüş Sistemi",
            14f
        )

        subtitle.gravity = Gravity.CENTER

        root.addView(subtitle)

        root.addView(
            text(
                "💰 Kasa Tutarı (TL)",
                15f,
                true
            )
        )

        kasa = EditText(this).apply {
            setText("3000")

            inputType =
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL

            setSingleLine()

            textSize = 19f

            gravity = Gravity.CENTER

            hint = "Örn. 3000 TL"
        }

        root.addView(
            kasa,
            LinearLayout.LayoutParams(
                -1,
                dp(55)
            )
        )

        root.addView(
            text(
                "⚽ Maç Sayısı",
                15f,
                true
            )
        )

        val tabs = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER

            setPadding(
                0,
                dp(5),
                0,
                dp(8)
            )
        }

        for (n in 2..8) {

            val button = Button(this).apply {

                text = n.toString()

                isAllCaps = false

                textSize = 13f

                setOnClickListener {

                    count = n

                    buildOdds()
                }
            }

            tabs.addView(
                button,
                LinearLayout.LayoutParams(
                    0,
                    dp(48),
                    1f
                ).apply {

                    setMargins(
                        dp(2),
                        0,
                        dp(2),
                        0
                    )
                }
            )
        }

        root.addView(tabs)

        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            addView(container)
        }

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        setContentView(root)

        buildOdds()
    }

    private fun buildOdds() {

        resultView = null

        container.removeAllViews()

        oddsFields.clear()

        container.addView(
            text(
                "📋 Oranları Gir",
                18f,
                true
            )
        )

        val defaultOdds = listOf(
            "1.60",
            "1.55",
            "1.75",
            "1.80",
            "1.65",
            "1.70",
            "1.85",
            "1.90"
        )

        for (i in 0 until count) {

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL

                setPadding(
                    0,
                    dp(2),
                    0,
                    dp(2)
                )
            }

            val matchLabel = text(
                "Maç ${i + 1}",
                15f,
                true
            )

            row.addView(
                matchLabel,
                LinearLayout.LayoutParams(
                    0,
                    dp(55),
                    0.40f
                )
            )

            val e = EditText(this).apply {

                setText(
                    defaultOdds[i]
                )

                inputType =
                    InputType.TYPE_CLASS_NUMBER or
                            InputType.TYPE_NUMBER_FLAG_DECIMAL

                setSingleLine()

                textSize = 18f

                gravity = Gravity.CENTER

                hint = "Oran"
            }

            oddsFields.add(e)

            row.addView(
                e,
                LinearLayout.LayoutParams(
                    0,
                    dp(55),
                    0.60f
                )
            )

            container.addView(row)
        }

        val calc = Button(this).apply {

            text = "🧮  HESAPLA"

            isAllCaps = false

            textSize = 18f

            setOnClickListener {
                calculate()
            }
        }

        container.addView(
            calc,
            LinearLayout.LayoutParams(
                -1,
                dp(58)
            ).apply {

                setMargins(
                    0,
                    dp(12),
                    0,
                    dp(12)
                )
            }
        )

        calculate()
    }

    private fun calculate() {

        resultView?.let {

            if (it.parent === container) {
                container.removeView(it)
            }
        }

        resultView = null

        val bankroll =
            kasa.text
                .toString()
                .replace(',', '.')
                .toDoubleOrNull()
                ?: 0.0

        val odds =
            oddsFields.map {

                it.text
                    .toString()
                    .replace(',', '.')
                    .toDoubleOrNull()
                    ?: 0.0
            }

        val valid =
            bankroll > 0.0 &&
                    odds.size == count &&
                    odds.all {
                        it > 0.0
                    }

        val result =
            LinearLayout(this).apply {

                orientation = LinearLayout.VERTICAL

                setPadding(
                    dp(14),
                    dp(12),
                    dp(14),
                    dp(16)
                )

                setBackgroundColor(
                    Color.WHITE
                )
            }

        resultView = result

        result.addView(
            text(
                "📊 SONUÇLAR",
                20f,
                true
            )
        )

        if (!valid) {

            result.addView(
                text(
                    "⚠️ Kasa ve tüm oranları geçerli giriniz.",
                    15f
                )
            )

            container.addView(result)

            return
        }

        var reciprocal = 0.0

        for (odd in odds) {
            reciprocal += 1.0 / odd
        }

        var total = 0.0

        for (i in odds.indices) {

            val odd = odds[i]

            val stake =
                bankroll /
                        (odd * reciprocal)

            val payout =
                stake * odd

            val net =
                payout - stake

            total += stake

            val line =
                TextView(this).apply {

                    text = String.format(
                        Locale.US,
                        "Maç %d   |   Oran %.2f\nYatırım: %.2f TL   |   Geri Dönüş: %.2f TL\nNet: %.2f TL",
                        i + 1,
                        odd,
                        stake,
                        payout,
                        net
                    )

                    textSize = 15f

                    setTextColor(
                        Color.rgb(
                            50,
                            50,
                            50
                        )
                    )

                    setPadding(
                        dp(4),
                        dp(10),
                        dp(4),
                        dp(10)
                    )
                }

            result.addView(line)
        }

        val payout =
            bankroll / reciprocal

        val divider =
            View(this).apply {

                setBackgroundColor(
                    Color.LTGRAY
                )
            }

        result.addView(
            divider,
            LinearLayout.LayoutParams(
                -1,
                dp(1)
            )
        )

        result.addView(
            text(
                String.format(
                    Locale.US,
                    "💵 Toplam Yatırım: %.2f TL",
                    total
                ),
                17f,
                true
            )
        )

        result.addView(
            text(
                String.format(
                    Locale.US,
                    "📈 Herhangi Biri Gelirse: %.2f TL",
                    payout
                ),
                17f,
                true
            )
        )

        result.addView(
            text(
                String.format(
                    Locale.US,
                    "💰 Kalan Kasa: %.2f TL",
                    bankroll - total
                ),
                17f,
                true
            )
        )

        result.addView(
            text(
                String.format(
                    Locale.US,
                    "📊 Geri Dönüş Oranı: %.2f%%",
                    (payout / bankroll) * 100
                ),
                17f,
                true
            )
        )

        container.addView(result)
    }
}
