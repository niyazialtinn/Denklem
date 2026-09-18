package com.niyazi.kuponhesaplayici

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import java.util.Locale

class MainActivity : android.app.Activity() {
    private lateinit var kasa: EditText
    private lateinit var container: LinearLayout
    private var count = 3
    private val oddsFields = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun text(v: String, size: Float, bold: Boolean=false) = TextView(this).apply {
        this.text=v; textSize=size; setTextColor(Color.rgb(30,30,30));
        if (bold) typeface=Typeface.DEFAULT_BOLD; setPadding(dp(4),dp(6),dp(4),dp(6))
    }

    private fun buildUi() {
        val root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(16),dp(12),dp(16),dp(16)); setBackgroundColor(Color.rgb(248,249,251)) }
        val title=text("Kupon Hesaplayıcı",24f,true); title.setTextColor(Color.rgb(21,101,192)); root.addView(title)
        root.addView(text("Excel'deki eşit geri dönüş hesabı",14f))

        val kasaLabel=text("Kasa (TL)",14f,true); root.addView(kasaLabel)
        kasa=EditText(this).apply { setText("3000"); inputType=InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL; setSingleLine(); textSize=18f; hint="Örn. 3000" }
        root.addView(kasa, LinearLayout.LayoutParams(-1,dp(52)))

        val tabs=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER; setPadding(0,dp(10),0,dp(8)) }
        listOf(2,3,4).forEach { n ->
            val b=Button(this).apply { text="$n Maç"; isAllCaps=false; setOnClickListener { count=n; buildOdds(); } }
            tabs.addView(b, LinearLayout.LayoutParams(0,dp(52),1f).apply { setMargins(dp(3),0,dp(3),0) })
        }
        root.addView(tabs)

        container=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
        root.addView(container, LinearLayout.LayoutParams(-1,0,1f))
        setContentView(root)
        buildOdds()
    }

    private fun buildOdds() {
        container.removeAllViews(); oddsFields.clear()
        container.addView(text("Oranları gir",18f,true))
        val defaults=when(count){2->listOf("1.60","1.55");3->listOf("1.60","1.55","1.75");else->listOf("1.60","1.55","1.75","1.80")}
        defaults.forEachIndexed { i, d ->
            val row=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
            row.addView(text("Maç ${i+1}",15f,true), LinearLayout.LayoutParams(0,dp(52),0.45f))
            val e=EditText(this).apply { setText(d); inputType=InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL; setSingleLine(); textSize=18f; gravity=Gravity.CENTER }
            oddsFields.add(e); row.addView(e, LinearLayout.LayoutParams(0,dp(52),0.55f))
            container.addView(row)
        }
        val calc=Button(this).apply { text="HESAPLA"; isAllCaps=false; textSize=17f; setOnClickListener { calculate() } }
        container.addView(calc, LinearLayout.LayoutParams(-1,dp(56)).apply { setMargins(0,dp(8),0,dp(8)) })
        calculate()
    }

    private fun calculate() {
        val bankroll=kasa.text.toString().replace(',','.').toDoubleOrNull() ?: 0.0
        val odds=oddsFields.map { it.text.toString().replace(',','.').toDoubleOrNull() ?: 0.0 }
        val valid=bankroll>0 && odds.all { it>0 }
        val result=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(12),dp(10),dp(12),dp(10)); setBackgroundColor(Color.WHITE) }
        result.addView(text("SONUÇLAR",18f,true))
        if(!valid){ result.addView(text("Kasa ve tüm oranları geçerli giriniz.",14f)); container.addView(result); return }
        val reciprocal=odds.sumOf { 1.0/it }
        var total=0.0
        odds.forEachIndexed { i,o ->
            val stake=bankroll/(o*reciprocal)
            val payout=stake*o
            val net=payout-stake
            total+=stake
            val line=TextView(this).apply { text=String.format(Locale.US,"Maç %d  |  Oran %.2f  |  Yatırım %.2f TL  |  Gelirse %.2f TL  |  Net %.2f TL",i+1,o,stake,payout,net); textSize=14f; setTextColor(Color.DKGRAY); setPadding(0,dp(7),0,dp(7)) }
            result.addView(line)
        }
        val payout = bankroll / reciprocal
        result.addView(text(String.format(Locale.US,"Toplam yatırım: %.2f TL",total),16f,true))
        result.addView(text(String.format(Locale.US,"Herhangi biri gelirse geri dönüş: %.2f TL",payout),16f,true))
        result.addView(text(String.format(Locale.US,"Kalan kasa: %.2f TL",bankroll-total),16f,true))
        result.addView(text(String.format(Locale.US,"Geri dönüş / başlangıç kasa: %.2f%%",(payout/bankroll)*100),16f,true))
        container.addView(result)
    }
}
