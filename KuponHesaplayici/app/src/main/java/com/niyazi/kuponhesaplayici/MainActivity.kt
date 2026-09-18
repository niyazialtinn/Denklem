package com.niyazi.kuponhesaplayici

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
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
private val redColor = Color.rgb(198, 40, 40)
private val orangeColor = Color.rgb(239, 130, 30)

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

private fun dp(value: Int): Int {
return (
value *
resources.displayMetrics.density
).toInt()
}

private fun makeText(
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

// =========================================================
// ANA EKRAN
// =========================================================

private fun buildUi() {

val root =
LinearLayout(this).apply {

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

// BAŞLIK

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
makeText(
"Kupon Hesaplayıcı",
25f,
true,
Color.WHITE
)
)

header.addView(
makeText(
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

// KASA

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
makeText(
"💰 KUPON KASASI",
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

hint =
"Örn. 3000 TL"

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

// MAÇ SAYISI

root.addView(
makeText(
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

for (number in 2..8) {

val button =
Button(this).apply {

text =
number.toString()

isAllCaps =
false

textSize =
14f

setPadding(
0,
0,
0,
0
)

setOnClickListener {

count =
number

buildOdds()
}
}

matchButtons.add(
button
)

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

// SCROLL

container =
LinearLayout(this).apply {

orientation =
LinearLayout.VERTICAL
}

val scroll =
ScrollView(this).apply {

isFillViewport =
true

addView(
container
)
}

root.addView(
scroll,
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

for (
index in matchButtons.indices
) {

val button =
matchButtons[index]

val number =
index + 2

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
makeText(
"🎯 ORANLAR",
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

card.addView(
makeText(
"Maç ${i + 1}",
15f,
true
),
LinearLayout.LayoutParams(
0,
dp(52),
0.45f
)
)

val edit =
EditText(this).apply {

setText(
defaultValues[i]
)

inputType =
InputType.TYPE_CLASS_NUMBER or
InputType.TYPE_NUMBER_FLAG_DECIMAL

setSingleLine()

textSize =
19f

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

oddsFields.add(edit)

card.addView(
edit,
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

// HESAPLA

val calculateButton =
Button(this).apply {

text =
"HESAPLA"

isAllCaps =
false

textSize =
18f

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
calculateButton,
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

// KAYDET

val saveButton =
Button(this).apply {

text =
"💾 HESAPLAMAYI KAYDET"

isAllCaps =
false

textSize =
16f

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

// İSTATİSTİK

val statisticsButton =
Button(this).apply {

text =
"📊 İSTATİSTİKLER"

isAllCaps =
false

textSize =
15f

typeface =
Typeface.DEFAULT_BOLD

setTextColor(
Color.WHITE
)

setBackgroundDrawable(
makeRoundedBackground(
greenColor,
16f
)
)

setOnClickListener {
showStatistics()
}
}

container.addView(
statisticsButton,
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

// KUPON YÖNETİMİ

val historyButton =
Button(this).apply {

text =
"📋 KUPON YÖNETİMİ"

isAllCaps =
false

textSize =
15f

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

resultView?.let { oldResult ->

if (
oldResult.parent ===
container
) {

container.removeView(
oldResult
)
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
mutableListOf<Double>()

for (field in oddsFields) {

val value =
field.text
.toString()
.replace(',', '.')
.toDoubleOrNull()
?: 0.0

odds.add(value)
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

resultView =
result

result.addView(
makeText(
"📊 SONUÇLAR",
18f,
true,
darkBlueColor
)
)

if (
bankroll <= 0.0 ||
odds.size != count ||
odds.any { it <= 0.0 }
) {

result.addView(
makeText(
"Kasa ve tüm oranları geçerli giriniz.",
14f,
false,
redColor
)
)

container.addView(
result
)

return
}

var reciprocal =
0.0

for (odd in odds) {

reciprocal +=
1.0 / odd
}

var total =
0.0

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

total +=
stake

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
makeText(
"Maç ${i + 1} • Oran ${
String.format(
Locale.US,
"%.2f",
odd
)
}",
15f,
true
)
)

matchCard.addView(
makeText(
String.format(
Locale.US,
"Yatırım: %.2f TL",
stake
),
14f
)
)

matchCard.addView(
makeText(
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
makeText(
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
makeText(
"💰 KUPON ÖZETİ",
16f,
true,
darkBlueColor
)
)

summary.addView(
makeText(
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
makeText(
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
makeText(
String.format(
Locale.US,
"Kalan kasa: %.2f TL",
bankroll - total
),
15f
)
)

summary.addView(
makeText(
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

container.addView(
result
)
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
mutableListOf<Double>()

for (field in oddsFields) {

val value =
field.text
.toString()
.replace(',', '.')
.toDoubleOrNull()
?: 0.0

odds.add(value)
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

val nameInput =
EditText(this).apply {

hint =
"Örn. Hafta Sonu Kuponu"

setSingleLine()

textSize =
16f

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
makeText(
"Bu kupona bir isim ver:",
15f,
true
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
AlertDialog.Builder(this)
.setTitle(
"💾 Kuponu Kaydet"
)
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
AlertDialog.BUTTON_POSITIVE
)

save.setOnClickListener {

var name =
nameInput.text
.toString()
.trim()

if (
name.isEmpty()
) {

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
// KUPON KAYDI
// =========================================================

private fun saveRecord(
name: String,
bankroll: Double,
odds: List<Double>
) {

var reciprocal =
0.0

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

val record =
JSONObject()

record.put(
"name",
name
)

record.put(
"date",
date
)

record.put(
"count",
odds.size
)

record.put(
"bankroll",
bankroll
)

record.put(
"payout",
payout
)

record.put(
"status",
"BEKLİYOR"
)

val oddsArray =
JSONArray()

for (odd in odds) {

oddsArray.put(
odd
)
}

record.put(
"odds",
oddsArray
)

val records =
getRecords()

records.put(
record
)

while (
records.length() > 50
) {

records.remove(0)
}

saveRecords(
records
)

Toast.makeText(
this,
"✅ \"$name\" kaydedildi.",
Toast.LENGTH_SHORT
).show()
}

// =========================================================
// KAYITLARI OKU
// =========================================================

private fun getRecords(): JSONArray {

val saved =
prefs.getString(
"records_json",
null
)

if (
!saved.isNullOrEmpty()
) {

try {

return JSONArray(
saved
)

} catch (_: Exception) {
// Eski kayıt sistemine geç
}
}

val oldRecords: Set<String> =
prefs.getStringSet(
"records",
emptySet()
) ?: emptySet()

val result =
JSONArray()

val oldList =
oldRecords.toList()

for (
index in oldList.indices
) {

val recordText =
oldList[index]

val parts =
recordText.split("|")

try {

if (
parts.size >= 6
) {

val obj =
JSONObject()

obj.put(
"name",
parts[0]
)

obj.put(
"date",
parts[1]
)

obj.put(
"count",
parts[2].toInt()
)

obj.put(
"bankroll",
parts[3].toDouble()
)

val oddsArray =
JSONArray()

val oddsList =
parts[4].split(",")

for (
oddsIndex in oddsList.indices
) {

oddsArray.put(
oddsList[oddsIndex]
.replace(
',',
'.'
)
.toDouble()
)
}

obj.put(
"odds",
oddsArray
)

obj.put(
"payout",
parts[5].toDouble()
)

obj.put(
"status",
"BEKLİYOR"
)

result.put(
obj
)

} else if (
parts.size >= 5
) {

val obj =
JSONObject()

obj.put(
"name",
"Eski Kupon"
)

obj.put(
"date",
parts[0]
)

obj.put(
"count",
parts[1].toInt()
)

obj.put(
"bankroll",
parts[2].toDouble()
)

val oddsArray =
JSONArray()

val oddsList =
parts[3].split(",")

for (
oddsIndex in oddsList.indices
) {

oddsArray.put(
oddsList[oddsIndex]
.replace(
',',
'.'
)
.toDouble()
)
}

obj.put(
"odds",
oddsArray
)

obj.put(
"payout",
parts[4].toDouble()
)

obj.put(
"status",
"BEKLİYOR"
)

result.put(
obj
)
}

} catch (_: Exception) {
// Hatalı eski kayıt atlanır
}
}

if (
result.length() > 0
) {

saveRecords(
result
)
}

return result
}

private fun saveRecords(
records: JSONArray
) {

prefs.edit()
.putString(
"records_json",
records.toString()
)
.apply()
}

// =========================================================
// İSTATİSTİKLER
// =========================================================

private fun showStatistics() {

val records =
getRecords()

if (
records.length() == 0
) {

Toast.makeText(
this,
"Henüz kayıtlı kupon yok.",
Toast.LENGTH_SHORT
).show()

return
}

var totalInvestment =
0.0

var totalPayout =
0.0

var realizedProfit =
0.0

var won =
0

var lost =
0

var waiting =
0

var highestPayout =
0.0

var highestProfit =
Double.NEGATIVE_INFINITY

for (
i in 0 until records.length()
) {

val obj =
records.getJSONObject(i)

val investment =
obj.optDouble(
"bankroll",
0.0
)

val payout =
obj.optDouble(
"payout",
0.0
)

val status =
obj.optString(
"status",
"BEKLİYOR"
)

totalInvestment +=
investment

when (status) {

"KAZANDI" -> {

won++

realizedProfit +=
payout - investment
}

"KAYBETTİ" -> {

lost++

realizedProfit -=
investment
}

else -> {

waiting++
}
}

if (
payout > highestPayout
) {

highestPayout =
payout
}

val couponProfit =
if (
status == "KAZANDI"
) {

payout - investment

} else if (
status == "KAYBETTİ"
) {

-investment

} else {

0.0
}

if (
status != "BEKLİYOR" &&
couponProfit > highestProfit
) {

highestProfit =
couponProfit
}

if (
status == "KAZANDI"
) {

totalPayout +=
payout
}
}

val completed =
won + lost

val winRate =
if (
completed > 0
) {

won.toDouble() /
completed.toDouble() *
100.0

} else {

0.0
}

val profitRate =
if (
totalInvestment > 0.0
) {

realizedProfit /
totalInvestment *
100.0

} else {

0.0
}

val startBankroll =
prefs.getString(
"starting_bankroll",
"0"
)
?.replace(',', '.')
?.toDoubleOrNull()
?: 0.0

val currentBankroll =
startBankroll +
realizedProfit

val layout =
LinearLayout(this).apply {

orientation =
LinearLayout.VERTICAL

setPadding(
dp(18),
dp(8),
dp(18),
dp(8)
)
}

// GÜNCEL BANKROLL

val balanceCard =
LinearLayout(this).apply {

orientation =
LinearLayout.VERTICAL

gravity =
Gravity.CENTER

setPadding(
dp(15),
dp(15),
dp(15),
dp(15)
)

setBackgroundDrawable(
makeRoundedBackground(
lightBlueColor,
17f
)
)
}

balanceCard.addView(
makeText(
"💰 GÜNCEL BANKROLL",
15f,
true,
darkBlueColor
)
)

balanceCard.addView(
makeText(
String.format(
Locale.US,
"%.2f TL",
currentBankroll
),
27f,
true,
if (
currentBankroll >=
startBankroll
) {
greenColor
} else {
redColor
}
)
)

layout.addView(
balanceCard,
LinearLayout.LayoutParams(
-1,
LinearLayout.LayoutParams.WRAP_CONTENT
).apply {

setMargins(
0,
0,
0,
dp(10)
)
}
)

// İSTATİSTİK KARTI

val stats =
LinearLayout(this).apply {

orientation =
LinearLayout.VERTICAL

setPadding(
dp(15),
dp(12),
dp(15),
dp(12)
)

setBackgroundDrawable(
makeRoundedBackground(
Color.WHITE,
17f,
Color.rgb(
225,
229,
235
)
)
)
}

stats.addView(
makeText(
"📊 KUPON İSTATİSTİKLERİ",
17f,
true,
darkBlueColor
)
)

stats.addView(
makeText(
"🎫 Toplam kupon: ${records.length()}",
14f
)
)

stats.addView(
makeText(
"🟢 Kazandı: $won",
14f,
true,
greenColor
)
)

stats.addView(
makeText(
"🔴 Kaybetti: $lost",
14f,
true,
redColor
)
)

stats.addView(
makeText(
"🟠 Bekliyor: $waiting",
14f,
true,
orangeColor
)
)

stats.addView(
makeText(
String.format(
Locale.US,
"🏆 Kazanma oranı: %.1f%%",
winRate
),
15f,
true
)
)

stats.addView(
makeText(
String.format(
Locale.US,
"💸 Toplam yatırım: %.2f TL",
totalInvestment
),
14f
)
)

stats.addView(
makeText(
String.format(
Locale.US,
"📈 Toplam geri dönüş: %.2f TL",
totalPayout
),
14f
)
)

val profitText =
if (
realizedProfit >= 0
) {

String.format(
Locale.US,
"🟢 NET KÂR: +%.2f TL",
realizedProfit
)

} else {

String.format(
Locale.US,
"🔴 NET ZARAR: %.2f TL",
realizedProfit
)
}

stats.addView(
makeText(
profitText,
19f,
true,
if (
realizedProfit >= 0
) {
greenColor
} else {
redColor
}
)
)

stats.addView(
makeText(
String.format(
Locale.US,
"Kâr / zarar oranı: %.1f%%",
profitRate
),
14f,
true
)
)

stats.addView(
makeText(
String.format(
Locale.US,
"🏆 En yüksek geri dönüş: %.2f TL",
highestPayout
),
14f
)
)

if (
highestProfit !=
Double.NEGATIVE_INFINITY
) {

stats.addView(
makeText(
String.format(
Locale.US,
"⭐ En yüksek net kâr: %.2f TL",
highestProfit
),
14f,
true,
greenColor
)
)
}

layout.addView(
stats,
LinearLayout.LayoutParams(
-1,
LinearLayout.LayoutParams.WRAP_CONTENT
).apply {

setMargins(
0,
0,
0,
dp(10)
)
}
)

// BANKROLL AYARLA

val bankrollButton =
Button(this).apply {

text =
"💰 BAŞLANGIÇ BANKROLL AYARLA"

isAllCaps =
false

textSize =
14f

setTextColor(
darkBlueColor
)

setBackgroundDrawable(
makeRoundedBackground(
lightBlueColor,
14f,
blueColor
)
)

setOnClickListener {
showBankrollDialog()
}
}

layout.addView(
bankrollButton,
LinearLayout.LayoutParams(
-1,
dp(52)
).apply {

setMargins(
0,
0,
0,
dp(8)
)
}
)

AlertDialog.Builder(this)
.setTitle(
"📊 İstatistikler"
)
.setView(layout)
.setPositiveButton(
"KAPAT",
null
)
.show()
}

// =========================================================
// BANKROLL AYARLA
// =========================================================

private fun showBankrollDialog() {

val input =
EditText(this).apply {

inputType =
InputType.TYPE_CLASS_NUMBER or
InputType.TYPE_NUMBER_FLAG_DECIMAL

setSingleLine()

textSize =
20f

gravity =
Gravity.CENTER

setPadding(
dp(12),
0,
dp(12),
0
)

val current =
prefs.getString(
"starting_bankroll",
""
)

if (
!current.isNullOrEmpty()
) {

setText(current)
}

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
makeText(
"Başlangıç bankroll tutarını gir:",
15f,
true
)
)

layout.addView(
input,
LinearLayout.LayoutParams(
-1,
dp(55)
).apply {

setMargins(
0,
dp(10),
0,
0
)
}
)

AlertDialog.Builder(this)
.setTitle(
"💰 Bankroll Ayarı"
)
.setView(layout)
.setNegativeButton(
"VAZGEÇ",
null
)
.setPositiveButton(
"KAYDET"
) { _, _ ->

val value =
input.text
.toString()
.replace(',', '.')
.toDoubleOrNull()

if (
value != null &&
value >= 0.0
) {

prefs.edit()
.putString(
"starting_bankroll",
value.toString()
)
.apply()

Toast.makeText(
this,
"✅ Başlangıç bankroll kaydedildi.",
Toast.LENGTH_SHORT
).show()
}
}
.show()
}

// =========================================================
// KUPON YÖNETİMİ
// =========================================================

private fun showHistory() {

val records =
getRecords()

if (
records.length() == 0
) {

Toast.makeText(
this,
"Henüz kayıtlı kupon yok.",
Toast.LENGTH_SHORT
).show()

return
}

val layout =
LinearLayout(this).apply {

orientation =
LinearLayout.VERTICAL

setPadding(
dp(18),
dp(8),
dp(18),
dp(8)
)
}

val scroll =
ScrollView(this)

val list =
LinearLayout(this).apply {

orientation =
LinearLayout.VERTICAL
}

for (
index in records.length() - 1 downTo 0
) {

val obj =
records.getJSONObject(index)

addHistoryCard(
list,
records,
index,
obj
)
}

scroll.addView(list)

layout.addView(
scroll,
LinearLayout.LayoutParams(
-1,
dp(470)
)
)

val clearButton =
Button(this).apply {

text =
"🗑 TÜM KUPONLARI SİL"

isAllCaps =
false

setTextColor(
redColor
)

setOnClickListener {
confirmDeleteAll()
}
}

layout.addView(
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

AlertDialog.Builder(this)
.setTitle(
"📋 Kupon Yönetimi"
)
.setView(layout)
.setPositiveButton(
"KAPAT",
null
)
.show()
}

// =========================================================
// KUPON KARTI
// =========================================================

private fun addHistoryCard(
list: LinearLayout,
records: JSONArray,
index: Int,
obj: JSONObject
) {

val name =
obj.optString(
"name",
"İsimsiz Kupon"
)

val date =
obj.optString(
"date",
"-"
)

val matchCount =
obj.optInt(
"count",
0
)

val bankroll =
obj.optDouble(
"bankroll",
0.0
)

val payout =
obj.optDouble(
"payout",
0.0
)

val status =
obj.optString(
"status",
"BEKLİYOR"
)

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
Color.WHITE,
15f,
Color.rgb(
225,
229,
235
)
)
)

setOnClickListener {

showHistoryDetail(
records,
index
)
}
}

val topRow =
LinearLayout(this).apply {

orientation =
LinearLayout.HORIZONTAL

gravity =
Gravity.CENTER_VERTICAL
}

topRow.addView(
makeText(
"🎫 $name",
17f,
true,
darkBlueColor
),
LinearLayout.LayoutParams(
0,
LinearLayout.LayoutParams.WRAP_CONTENT,
1f
)
)

topRow.addView(
statusView(status),
LinearLayout.LayoutParams(
dp(105),
dp(34)
)
)

card.addView(topRow)

card.addView(
makeText(
"📅 $date",
13f,
false,
grayTextColor
)
)

card.addView(
makeText(
"⚽ $matchCount maç • 💰 Kasa: ${
String.format(
Locale.US,
"%.2f",
bankroll
)
} TL",
14f
)
)

card.addView(
makeText(
"📈 Geri dönüş: ${
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

val statusRow =
LinearLayout(this).apply {

orientation =
LinearLayout.HORIZONTAL
}

val waiting =
smallStatusButton(
"🟠 Bekliyor",
orangeColor
)

waiting.setOnClickListener {

updateStatus(
records,
index,
"BEKLİYOR"
)
}

val won =
smallStatusButton(
"🟢 Kazandı",
greenColor
)

won.setOnClickListener {

updateStatus(
records,
index,
"KAZANDI"
)
}

val lost =
smallStatusButton(
"🔴 Kaybetti",
redColor
)

lost.setOnClickListener {

updateStatus(
records,
index,
"KAYBETTİ"
)
}

statusRow.addView(
waiting,
LinearLayout.LayoutParams(
0,
dp(42),
1f
).apply {

setMargins(
0,
dp(8),
dp(3),
0
)
}
)

statusRow.addView(
won,
LinearLayout.LayoutParams(
0,
dp(42),
1f
).apply {

setMargins(
dp(3),
dp(8),
dp(3),
0
)
}
)

statusRow.addView(
lost,
LinearLayout.LayoutParams(
0,
dp(42),
1f
).apply {

setMargins(
dp(3),
dp(8),
0,
0
)
}
)

card.addView(
statusRow
)

val delete =
Button(this).apply {

text =
"🗑 Kuponu Sil"

isAllCaps =
false

textSize =
13f

setTextColor(
redColor
)

setBackgroundDrawable(
makeRoundedBackground(
Color.rgb(
255,
245,
245
),
10f
)
)

setOnClickListener {

confirmDeleteSingle(
records,
index,
name
)
}
}

card.addView(
delete,
LinearLayout.LayoutParams(
-1,
dp(42)
).apply {

setMargins(
0,
dp(7),
0,
0
)
}
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
// DURUM GÖRÜNÜMÜ
// =========================================================

private fun statusView(
status: String
): TextView {

val color =
when (status) {

"KAZANDI" ->
greenColor

"KAYBETTİ" ->
redColor

else ->
orangeColor
}

return makeText(
status,
12f,
true,
color
).apply {

gravity =
Gravity.CENTER

setBackgroundDrawable(
makeRoundedBackground(
Color.WHITE,
10f,
color
)
)
}
}

// =========================================================
// DURUM BUTONU
// =========================================================

private fun smallStatusButton(
label: String,
color: Int
): Button {

return Button(this).apply {

text =
label

isAllCaps =
false

textSize =
11f

setTextColor(
color
)

setPadding(
0,
0,
0,
0
)

setBackgroundDrawable(
makeRoundedBackground(
Color.WHITE,
10f,
color
)
)
}
}

// =========================================================
// DURUM GÜNCELLE
// =========================================================

private fun updateStatus(
records: JSONArray,
index: Int,
status: String
) {

try {

val obj =
records.getJSONObject(index)

obj.put(
"status",
status
)

saveRecords(
records
)

Toast.makeText(
this,
"Durum: $status",
Toast.LENGTH_SHORT
).show()

showHistory()

} catch (_: Exception) {

Toast.makeText(
this,
"Durum güncellenemedi.",
Toast.LENGTH_SHORT
).show()
}
}

// =========================================================
// DETAY
// =========================================================

private fun showHistoryDetail(
records: JSONArray,
index: Int
) {

try {

val obj =
records.getJSONObject(index)

val name =
obj.optString(
"name",
"İsimsiz Kupon"
)

val date =
obj.optString(
"date",
"-"
)

val matchCount =
obj.optInt(
"count",
0
)

val bankroll =
obj.optDouble(
"bankroll",
0.0
)

val payout =
obj.optDouble(
"payout",
0.0
)

val status =
obj.optString(
"status",
"BEKLİYOR"
)

val odds =
obj.optJSONArray(
"odds"
)

val message =
StringBuilder()

message.append(
"🎫 Kupon: $name\n\n"
)

message.append(
"📅 Tarih: $date\n\n"
)

message.append(
"📌 Durum: $status\n\n"
)

message.append(
"⚽ Maç sayısı: $matchCount\n\n"
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
"🎯 Oranlar:\n"
)

if (
odds != null
) {

for (
i in 0 until odds.length()
) {

message.append(
"Maç ${i + 1}: "
)

message.append(
String.format(
Locale.US,
"%.2f",
odds.optDouble(i)
)
)

message.append(
"\n"
)
}
}

message.append(
"\n📈 Geri dönüş: "
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

AlertDialog.Builder(this)
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

} catch (_: Exception) {

Toast.makeText(
this,
"Kupon detayı açılamadı.",
Toast.LENGTH_SHORT
).show()
}
}

// =========================================================
// TEK KUPON SİL
// =========================================================

private fun confirmDeleteSingle(
records: JSONArray,
index: Int,
name: String
) {

AlertDialog.Builder(this)
.setTitle(
"Kuponu Sil"
)
.setMessage(
"\"$name\" kuponu silinsin mi?"
)
.setNegativeButton(
"VAZGEÇ",
null
)
.setPositiveButton(
"SİL"
) { _, _ ->

val newRecords =
JSONArray()

for (
i in 0 until records.length()
) {

if (
i != index
) {

newRecords.put(
records.getJSONObject(i)
)
}
}

saveRecords(
newRecords
)

Toast.makeText(
this,
"🗑 Kupon silindi.",
Toast.LENGTH_SHORT
).show()

showHistory()
}
.show()
}

// =========================================================
// TÜMÜNÜ SİL
// =========================================================

private fun confirmDeleteAll() {

AlertDialog.Builder(this)
.setTitle(
"Tüm Kuponları Sil"
)
.setMessage(
"Kayıtlı tüm kuponlar silinecek. Emin misin?"
)
.setNegativeButton(
"VAZGEÇ",
null
)
.setPositiveButton(
"SİL"
) { _, _ ->

prefs.edit()
.remove(
"records_json"
)
.remove(
"records"
)
.apply()

Toast.makeText(
this,
"🗑 Tüm kuponlar silindi.",
Toast.LENGTH_SHORT
).show()
}
.show()
}
}
