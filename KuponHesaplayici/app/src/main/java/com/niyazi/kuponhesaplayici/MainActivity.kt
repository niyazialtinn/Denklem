package com.niyazi.kuponhesaplayici

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var kasa: EditText
    private lateinit var container: LinearLayout

    private var count = 3

    private val oddsFields = mutableListOf<EditText>()
    private val matchButtons = mutableListOf<Button>()

    private var resultView: LinearLayout? = null

    private val blueColor = Color.rgb(21, 101, 192)
    private val darkBlueColor = Color.rgb(13, 71, 161)
    private val lightBlueColor = Color.rgb(232, 240, 254)
    private val pageBackgroundColor = Color.rgb(246, 248, 252)
    private val darkTextColor = Color.rgb(30, 35, 45)
    private val grayTextColor = Color.rgb(100, 108, 120)
    private val greenColor = Color.rgb(46, 125, 50)
    private val redColor = Color.rgb(190, 40, 40)

    private val prefs by lazy {
        getSharedPreferences(
            "kupon_gecmis",
            Context.MODE_PRIVATE
        )
    }

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
        bold: Boolean = false,
        color: Int = darkTextColor
    ): TextView {

        return TextView(this).apply {

            text = value
            textSize = size
            setTextColor(color)

            if (bold) {
                typeface = Typeface.DEFAULT_BOLD
            }

            setPadding(
                dp(4),
                dp(5),
                dp(4),
                dp(5)
            )
        }
    }

    private fun makeRoundedBackground(
        fillColor: Int,
        radius: Float = 18f,
        strokeColor: Int? = null
    ): GradientDrawable {

        return GradientDrawable().apply {

            setColor(fillColor)

            cornerRadius =
                dp(radius.toInt()).toFloat()

            if (strokeColor != null) {
                setStroke(
                    dp(1),
                    strokeColor
                )
            }
        }
    }

    private fun buildUi() {

        val root = LinearLayout(this).apply {

            orientation =
                LinearLayout.VERTICAL

            setPadding(
                dp(16),
                dp(14),
                dp(16),
                dp(16)
            )

            setBackgroundColor(
                pageBackgroundColor
            )
        }

        // =====================================================
        // BAŞLIK
        // =====================================================

        val header =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(18),
                    dp(16),
                    dp(18),
                    dp(16)
                )

                setBackgroundDrawable(
                    makeRoundedBackground(
                        blueColor,
                        20f
                    )
                )
            }

        header.addView(
            text(
                "Kupon Hesaplayıcı",
                25f,
                true,
                Color.WHITE
            )
        )

        header.addView(
            text(
                "Eşit geri dönüş hesaplama",
                14f,
                false,
                Color.WHITE
            )
        )

        root.addView(
            header,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {

                setMargins(
                    0,
                    0,
                    0,
                    dp(14)
                )
            }
        )

        // =====================================================
        // KASA
        // =====================================================

        val kasaCard =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(16),
                    dp(12),
                    dp(16),
                    dp(14)
                )

                setBackgroundDrawable(
                    makeRoundedBackground(
                        Color.WHITE,
                        18f,
                        Color.rgb(
                            225,
                            229,
                            235
                        )
                    )
                )
            }

        kasaCard.addView(
            text(
                "💰  KASA",
                15f,
                true,
                darkBlueColor
            )
        )

        kasa =
            EditText(this).apply {

                setText("3000")

                inputType =
                    InputType.TYPE_CLASS_NUMBER or
                            InputType.TYPE_NUMBER_FLAG_DECIMAL

                setSingleLine()

                textSize = 22f

                gravity =
                    Gravity.CENTER_VERTICAL

                setTextColor(
                    darkTextColor
                )

                setSelectAllOnFocus(true)

                hint = "Örn. 3000 TL"

                setBackgroundDrawable(
                    makeRoundedBackground(
                        Color.rgb(
                            248,
                            249,
                            252
                        ),
                        12f,
                        Color.rgb(
                            210,
                            215,
                            223
                        )
                    )
                )

                setPadding(
                    dp(12),
                    0,
                    dp(12),
                    0
                )
            }

        kasaCard.addView(
            kasa,
            LinearLayout.LayoutParams(
                -1,
                dp(56)
            ).apply {

                setMargins(
                    0,
                    dp(8),
                    0,
                    0
                )
            }
        )

        root.addView(
            kasaCard,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {

                setMargins(
                    0,
                    0,
                    0,
                    dp(12)
                )
            }
        )

        // =====================================================
        // MAÇ SEÇİMİ
        // =====================================================

        root.addView(
            text(
                "Kaç maç?",
                16f,
                true
            )
        )

        val tabs =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    dp(7),
                    0,
                    dp(12)
                )
            }

        matchButtons.clear()

        for (n in 2..8) {

            val button =
                Button(this).apply {

                    text = n.toString()

                    isAllCaps = false

                    textSize = 14f

                    setPadding(
                        0,
                        0,
                        0,
                        0
                    )

                    setOnClickListener {

                        count = n

                        buildOdds()
                    }
                }

            matchButtons.add(button)

            tabs.addView(
                button,
                LinearLayout.LayoutParams(
                    0,
                    dp(46),
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

        // =====================================================
        // SCROLL
        // =====================================================

        container =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL
            }

        val scrollView =
            ScrollView(this).apply {

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

    // =========================================================
    // MAÇ BUTONLARI
    // =========================================================

    private fun updateMatchButtons() {

        for (i in matchButtons.indices) {

            val button =
                matchButtons[i]

            val number = i + 2

            if (number == count) {

                button.setBackgroundDrawable(
                    makeRoundedBackground(
                        blueColor,
                        12f
                    )
                )

                button.setTextColor(
                    Color.WHITE
                )

            } else {

                button.setBackgroundDrawable(
                    makeRoundedBackground(
                        Color.WHITE,
                        12f,
                        Color.rgb(
                            220,
                            224,
                            230
                        )
                    )
                )

                button.setTextColor(
                    darkTextColor
                )
            }
        }
    }

    // =========================================================
    // ORANLAR
    // =========================================================

    private fun buildOdds() {

        resultView = null

        container.removeAllViews()

        oddsFields.clear()

        updateMatchButtons()

        container.addView(
            text(
                "🎯  ORANLAR",
                16f,
                true,
                darkBlueColor
            )
        )

        val defaultValues =
            listOf(
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

            val card =
                LinearLayout(this).apply {

                    orientation =
                        LinearLayout.HORIZONTAL

                    gravity =
                        Gravity.CENTER_VERTICAL

                    setPadding(
                        dp(14),
                        dp(6),
                        dp(10),
                        dp(6)
                    )

                    setBackgroundDrawable(
                        makeRoundedBackground(
                            Color.WHITE,
                            15f,
                            Color.rgb(
                                225,
                                229,
                                235
                            )
                        )
                    )
                }

            val matchLabel =
                text(
                    "Maç ${i + 1}",
                    15f,
                    true
                )

            card.addView(
                matchLabel,
                LinearLayout.LayoutParams(
                    0,
                    dp(52),
                    0.45f
                )
            )

            val e =
                EditText(this).apply {

                    setText(
                        defaultValues[i]
                    )

                    inputType =
                        InputType.TYPE_CLASS_NUMBER or
                                InputType.TYPE_NUMBER_FLAG_DECIMAL

                    setSingleLine()

                    textSize = 19f

                    gravity =
                        Gravity.CENTER

                    setTextColor(
                        darkBlueColor
                    )

                    typeface =
                        Typeface.DEFAULT_BOLD

                    setBackgroundDrawable(
                        makeRoundedBackground(
                            lightBlueColor,
                            12f
                        )
                    )

                    setPadding(
                        dp(8),
                        0,
                        dp(8),
                        0
                    )
                }

            oddsFields.add(e)

            card.addView(
                e,
                LinearLayout.LayoutParams(
                    0,
                    dp(48),
                    0.55f
                )
            )

            container.addView(
                card,
                LinearLayout.LayoutParams(
                    -1,
                    dp(64)
                ).apply {

                    setMargins(
                        0,
                        dp(4),
                        0,
                        dp(4)
                    )
                }
            )
        }

        // =====================================================
        // HESAPLA
        // =====================================================

        val calc =
            Button(this).apply {

                text = "HESAPLA"

                isAllCaps = false

                textSize = 18f

                typeface =
                    Typeface.DEFAULT_BOLD

                setTextColor(
                    Color.WHITE
                )

                setBackgroundDrawable(
                    makeRoundedBackground(
                        darkBlueColor,
                        16f
                    )
                )

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
                    dp(8)
                )
            }
        )

        // =====================================================
        // KAYDET
        // =====================================================

        val saveButton =
            Button(this).apply {

                text =
                    "💾  HESAPLAMAYI KAYDET"

                isAllCaps = false

                textSize = 16f

                typeface =
                    Typeface.DEFAULT_BOLD

                setTextColor(
                    darkBlueColor
                )

                setBackgroundDrawable(
                    makeRoundedBackground(
                        lightBlueColor,
                        16f,
                        blueColor
                    )
                )

                setOnClickListener {

                    saveCalculation()
                }
            }

        container.addView(
            saveButton,
            LinearLayout.LayoutParams(
                -1,
                dp(54)
            ).apply {

                setMargins(
                    0,
                    0,
                    0,
                    dp(8)
                )
            }
        )

        // =====================================================
        // GEÇMİŞ
        // =====================================================

        val historyButton =
            Button(this).apply {

                text =
                    "📋  GEÇMİŞ HESAPLAMALAR"

                isAllCaps = false

                textSize = 15f

                typeface =
                    Typeface.DEFAULT_BOLD

                setTextColor(
                    Color.WHITE
                )

                setBackgroundDrawable(
                    makeRoundedBackground(
                        blueColor,
                        16f
                    )
                )

                setOnClickListener {

                    showHistory()
                }
            }

        container.addView(
            historyButton,
            LinearLayout.LayoutParams(
                -1,
                dp(54)
            ).apply {

                setMargins(
                    0,
                    0,
                    0,
                    dp(14)
                )
            }
        )

        calculate()
    }

    // =========================================================
    // HESAPLAMA
    // =========================================================

    private fun calculate() {

        val imm =
            getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

        imm.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            0
        )

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
                    odds.size ==
                    oddsFields.size &&
                    odds.all {
                        it > 0.0
                    }

        val result =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(14),
                    dp(14),
                    dp(14),
                    dp(14)
                )

                setBackgroundDrawable(
                    makeRoundedBackground(
                        Color.WHITE,
                        18f,
                        Color.rgb(
                            225,
                            229,
                            235
                        )
                    )
                )
            }

        resultView = result

        result.addView(
            text(
                "📊  SONUÇLAR",
                18f,
                true,
                darkBlueColor
            )
        )

        if (!valid) {

            result.addView(
                text(
                    "Kasa ve tüm oranları geçerli giriniz.",
                    14f,
                    false,
                    redColor
                )
            )

            container.addView(result)

            return
        }

        var reciprocal = 0.0

        for (odd in odds) {

            reciprocal +=
                1.0 / odd
        }

        var total = 0.0

        for (i in odds.indices) {

            val odd =
                odds[i]

            val stake =
                bankroll /
                        (odd * reciprocal)

            val payout =
                stake * odd

            val net =
                payout - stake

            total += stake

            val matchCard =
                LinearLayout(this).apply {

                    orientation =
                        LinearLayout.VERTICAL

                    setPadding(
                        dp(12),
                        dp(9),
                        dp(12),
                        dp(9)
                    )

                    setBackgroundDrawable(
                        makeRoundedBackground(
                            Color.rgb(
                                248,
                                250,
                                253
                            ),
                            13f,
                            Color.rgb(
                                225,
                                229,
                                235
                            )
                        )
                    )
                }

            matchCard.addView(
                text(
                    "Maç ${i + 1}   •   Oran ${
                        String.format(
                            Locale.US,
                            "%.2f",
                            odd
                        )
                    }",
                    15f,
                    true,
                    darkTextColor
                )
            )

            matchCard.addView(
                text(
                    String.format(
                        Locale.US,
                        "Yatırım: %.2f TL",
                        stake
                    ),
                    14f
                )
            )

            matchCard.addView(
                text(
                    String.format(
                        Locale.US,
                        "Gelirse: %.2f TL",
                        payout
                    ),
                    14f,
                    true,
                    greenColor
                )
            )

            matchCard.addView(
                text(
                    String.format(
                        Locale.US,
                        "Net: %.2f TL",
                        net
                    ),
                    13f,
                    false,
                    grayTextColor
                )
            )

            result.addView(
                matchCard,
                LinearLayout.LayoutParams(
                    -1,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {

                    setMargins(
                        0,
                        dp(5),
                        0,
                        dp(5)
                    )
                }
            )
        }

        val payout =
            bankroll / reciprocal

        // =====================================================
        // ÖZET
        // =====================================================

        val summary =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(14),
                    dp(12),
                    dp(14),
                    dp(12)
                )

                setBackgroundDrawable(
                    makeRoundedBackground(
                        lightBlueColor,
                        15f
                    )
                )
            }

        summary.addView(
            text(
                "💰  KUPON ÖZETİ",
                16f,
                true,
                darkBlueColor
            )
        )

        summary.addView(
            text(
                String.format(
                    Locale.US,
                    "Toplam yatırım: %.2f TL",
                    total
                ),
                15f,
                true
            )
        )

        summary.addView(
            text(
                String.format(
                    Locale.US,
                    "Herhangi biri gelirse: %.2f TL",
                    payout
                ),
                18f,
                true,
                greenColor
            )
        )

        summary.addView(
            text(
                String.format(
                    Locale.US,
                    "Kalan kasa: %.2f TL",
                    bankroll - total
                ),
                15f
            )
        )

        summary.addView(
            text(
                String.format(
                    Locale.US,
                    "Geri dönüş oranı: %.2f%%",
                    (payout / bankroll) * 100
                ),
                15f,
                true,
                darkBlueColor
            )
        )

        result.addView(
            summary,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {

                setMargins(
                    0,
                    dp(12),
                    0,
                    dp(4)
                )
            }
        )

        container.addView(result)
    }

    // =========================================================
    // KUPON KAYDET
    // =========================================================

    private fun saveCalculation() {

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

        if (
            bankroll <= 0.0 ||
            odds.any { it <= 0.0 }
        ) {

            Toast.makeText(
                this,
                "Önce geçerli kasa ve oranları gir.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =====================================================
        // İSİM SOR
        // =====================================================

        val nameInput =
            EditText(this).apply {

                hint =
                    "Örn. Hafta Sonu Kuponu"

                setSingleLine()

                textSize = 16f

                setPadding(
                    dp(12),
                    0,
                    dp(12),
                    0
                )

                setBackgroundDrawable(
                    makeRoundedBackground(
                        Color.rgb(
                            248,
                            249,
                            252
                        ),
                        12f,
                        Color.rgb(
                            210,
                            215,
                            223
                        )
                    )
                )
            }

        val layout =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(20),
                    dp(5),
                    dp(20),
                    dp(5)
                )
            }

        layout.addView(
            text(
                "Bu kupona bir isim ver:",
                15f,
                true,
                darkTextColor
            )
        )

        layout.addView(
            nameInput,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {

                setMargins(
                    0,
                    dp(10),
                    0,
                    0
                )
            }
        )

        val dialog =
            android.app.AlertDialog.Builder(this)
                .setTitle("💾 Kuponu Kaydet")
                .setView(layout)
                .setNegativeButton(
                    "VAZGEÇ",
                    null
                )
                .setPositiveButton(
                    "KAYDET",
                    null
                )
                .create()

        dialog.setOnShowListener {

            val save =
                dialog.getButton(
                    android.app.AlertDialog.BUTTON_POSITIVE
                )

            save.setOnClickListener {

                var name =
                    nameInput.text
                        .toString()
                        .trim()

                if (name.isEmpty()) {

                    name =
                        "İsimsiz Kupon"
                }

                saveRecord(
                    name,
                    bankroll,
                    odds
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // =========================================================
    // KAYDI OLUŞTUR
    // =========================================================

    private fun saveRecord(
        name: String,
        bankroll: Double,
        odds: List<Double>
    ) {

        var reciprocal = 0.0

        for (odd in odds) {
            reciprocal +=
                1.0 / odd
        }

        val payout =
            bankroll / reciprocal

        val date =
            SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())

        val oddsText =
            odds.joinToString(",")

        /*
         * Yeni kayıt formatı:
         *
         * isim|tarih|maçsayısı|kasa|oranlar|geridönüş
         */

        val record =
            "$name|$date|$count|$bankroll|$oddsText|$payout"

        val oldRecords =
            prefs.getStringSet(
                "records",
                emptySet()
            )?.toMutableList()
                ?: mutableListOf()

        oldRecords.add(record)

        // Son 50 kayıt
        val lastRecords =
            oldRecords.takeLast(50)

        prefs.edit()
            .putStringSet(
                "records",
                lastRecords.toSet()
            )
            .apply()

        Toast.makeText(
            this,
            "✅ \"$name\" kaydedildi.",
            Toast.LENGTH_SHORT
        ).show()
    }

    // =========================================================
    // GEÇMİŞ
    // =========================================================

    private fun showHistory() {

        val records =
            prefs.getStringSet(
                "records",
                emptySet()
            )?.toList()
                ?.reversed()
                ?: emptyList()

        if (records.isEmpty()) {

            Toast.makeText(
                this,
                "Henüz kayıtlı hesaplama yok.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val dialogLayout =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(20),
                    dp(10),
                    dp(20),
                    dp(10)
                )
            }

        dialogLayout.addView(
            text(
                "Kayıtlı kuponların:",
                15f,
                false,
                grayTextColor
            )
        )

        val scroll =
            ScrollView(this)

        val list =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL
            }

        for (record in records) {

            val parts =
                record.split("|")

            // Yeni kayıt formatı
            if (parts.size >= 6) {

                val name = parts[0]
                val date = parts[1]
                val matchCount = parts[2]
                val bankroll =
                    parts[3].toDoubleOrNull()
                        ?: 0.0

                val odds =
                    parts[4].split(",")

                val payout =
                    parts[5].toDoubleOrNull()
                        ?: 0.0

                addHistoryCard(
                    list,
                    name,
                    date,
                    matchCount,
                    bankroll,
                    odds,
                    payout
                )

            } else if (parts.size >= 5) {

                // Eski kayıt formatı
                val name =
                    "Eski Kupon"

                val date = parts[0]

                val matchCount =
                    parts[1]

                val bankroll =
                    parts[2].toDoubleOrNull()
                        ?: 0.0

                val odds =
                    parts[3].split(",")

                val payout =
                    parts[4].toDoubleOrNull()
                        ?: 0.0

                addHistoryCard(
                    list,
                    name,
                    date,
                    matchCount,
                    bankroll,
                    odds,
                    payout
                )
            }
        }

        scroll.addView(list)

        dialogLayout.addView(
            scroll,
            LinearLayout.LayoutParams(
                -1,
                dp(430)
            )
        )

        // =====================================================
        // TÜM GEÇMİŞİ SİL
        // =====================================================

        val clearButton =
            Button(this).apply {

                text =
                    "🗑  TÜM GEÇMİŞİ SİL"

                isAllCaps = false

                setTextColor(
                    redColor
                )

                setOnClickListener {

                    confirmDeleteHistory()
                }
            }

        dialogLayout.addView(
            clearButton,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {

                setMargins(
                    0,
                    dp(8),
                    0,
                    0
                )
            }
        )

        android.app.AlertDialog.Builder(this)
            .setTitle(
                "📋 Geçmiş Hesaplamalar"
            )
            .setView(dialogLayout)
            .setPositiveButton(
                "KAPAT",
                null
            )
            .show()
    }

    // =========================================================
    // GEÇMİŞ KARTI
    // =========================================================

    private fun addHistoryCard(
        list: LinearLayout,
        name: String,
        date: String,
        matchCount: String,
        bankroll: Double,
        odds: List<String>,
        payout: Double
    ) {

        val card =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(14),
                    dp(11),
                    dp(14),
                    dp(11)
                )

                setBackgroundDrawable(
                    makeRoundedBackground(
                        Color.rgb(
                            248,
                            250,
                            253
                        ),
                        14f,
                        Color.rgb(
                            225,
                            229,
                            235
                        )
                    )
                )

                setOnClickListener {

                    showHistoryDetail(
                        name,
                        date,
                        matchCount,
                        bankroll,
                        odds,
                        payout
                    )
                }
            }

        // KUPON ADI
        card.addView(
            text(
                "🎫  $name",
                17f,
                true,
                darkBlueColor
            )
        )

        // TARİH
        card.addView(
            text(
                "📅 $date",
                13f,
                false,
                grayTextColor
            )
        )

        // MAÇ + KASA
        card.addView(
            text(
                "⚽ $matchCount maç   •   Kasa: ${
                    String.format(
                        Locale.US,
                        "%.2f",
                        bankroll
                    )
                } TL",
                14f
            )
        )

        // GERİ DÖNÜŞ
        card.addView(
            text(
                "💰 Geri dönüş: ${
                    String.format(
                        Locale.US,
                        "%.2f",
                        payout
                    )
                } TL",
                14f,
                true,
                greenColor
            )
        )

        list.addView(
            card,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {

                setMargins(
                    0,
                    dp(5),
                    0,
                    dp(5)
                )
            }
        )
    }

    // =========================================================
    // KUPON DETAYI
    // =========================================================

    private fun showHistoryDetail(
        name: String,
        date: String,
        matchCount: String,
        bankroll: Double,
        odds: List<String>,
        payout: Double
    ) {

        val message =
            StringBuilder()

        message.append(
            "🎫 Kupon: $name\n\n"
        )

        message.append(
            "📅 Tarih: $date\n\n"
        )

        message.append(
            "💰 Kasa: "
        )

        message.append(
            String.format(
                Locale.US,
                "%.2f",
                bankroll
            )
        )

        message.append(
            " TL\n\n"
        )

        message.append(
            "⚽ Maç sayısı: $matchCount\n\n"
        )

        message.append(
            "🎯 Oranlar:\n"
        )

        for (i in odds.indices) {

            message.append(
                "Maç ${i + 1}: ${odds[i]}\n"
            )
        }

        message.append(
            "\n💵 Geri dönüş: "
        )

        message.append(
            String.format(
                Locale.US,
                "%.2f",
                payout
            )
        )

        message.append(
            " TL"
        )

        android.app.AlertDialog.Builder(this)
            .setTitle(
                "🎫 Kupon Detayı"
            )
            .setMessage(
                message.toString()
            )
            .setPositiveButton(
                "KAPAT",
                null
            )
            .show()
    }

    // =========================================================
    // GEÇMİŞİ SİL
    // =========================================================

    private fun confirmDeleteHistory() {

        android.app.AlertDialog.Builder(this)
            .setTitle(
                "Geçmişi Sil"
            )
            .setMessage(
                "Tüm kayıtlı hesaplamalar silinecek. Emin misin?"
            )
            .setNegativeButton(
                "VAZGEÇ",
                null
            )
            .setPositiveButton(
                "SİL"
            ) { _, _ ->

                prefs.edit()
                    .remove("records")
                    .apply()

                Toast.makeText(
                    this,
                    "🗑 Geçmiş temizlendi.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }
}
