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
value * resources.displayMetrics.density
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

isAllCaps = false

textSize = 14f

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

container =
LinearLayout(this).apply {

orientation =
LinearLayout.VERTICAL
}

val scroll =
ScrollView(this).apply {

isFillViewport = true

addView(container)
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

private fun updateMatchButtons() {

for (index in matchButtons.indices) {

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

val calculateButton =
Button(this).apply {

text =
"HESAPLA"

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

val saveButton =
Button(this).apply {

text =
"💾 HESAPLAMAYI KAYDET"

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

val statisticsButton =
Button(this).apply {

text =
"📊 İSTATİSTİKLER"

isAllCaps = false

textSize = 15f

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

val historyButton =
Button(this).apply {

text =
"📋 KUPON YÖNETİMİ"

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

resultView = result

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

container.addView(result)

return
}

var reciprocal = 0.0

for (odd in odds) {
reciprocal += 1.0 / odd
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

var reciprocal = 0.0

for (odd in odds) {
reciprocal += 1.0 / odd
}

val totalPayout =
bankroll / reciprocal

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
odds,
totalPayout
)

dialog.dismiss()
}
}

dialog.show()
}

// =========================================================
// YENİ KUPON KAYDI
// =========================================================

private fun saveRecord(
name: String,
bankroll: Double,
odds: List<Double>,
totalPayout: Double
) {

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
totalPayout
)

// =====================================================
// HER MAÇ ARTIK AYRI BİR OBJE
// =====================================================

val matches =
JSONArray()

var reciprocal = 0.0

for (odd in odds) {
reciprocal += 1.0 / odd
}

for (i in odds.indices) {

val odd =
odds[i]

val stake =
bankroll /
(odd * reciprocal)

val payout =
stake * odd

val match =
JSONObject()

match.put(
"number",
i + 1
)

match.put(
"odd",
odd
)

match.put(
"stake",
stake
)

match.put(
"payout",
payout
)

match.put(
"status",
"BEKLİYOR"
)

matches.put(
match
)
}

record.put(
"matches",
matches
)

// Eski uyumluluk için oranları da tutuyoruz.
val oddsArray =
JSONArray()

for (odd in odds) {
oddsArray.put(odd)
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

saveRecords(records)

Toast.makeText(
this,
"✅ \"$name\" kaydedildi.",
Toast.LENGTH_SHORT
).show()
}

// =========================================================
// KAYITLARI AL
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

val records =
JSONArray(saved)

migrateRecords(records)

return records

} catch (_: Exception) {
}
}

// Eski V2/V3 kayıt sistemi
val oldRecords: Set<String> =
prefs.getStringSet(
"records",
emptySet()
) ?: emptySet()

val result =
JSONArray()

val oldList =
oldRecords.toList()

for (index in oldList.indices) {

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

result.put(obj)

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

result.put(obj)
}

} catch (_: Exception) {
}
}

migrateRecords(result)

if (
result.length() > 0
) {
saveRecords(result)
}

return result
}

// =========================================================
// ESKİ KAYITLARI YENİ MAÇ SİSTEMİNE ÇEVİR
// =========================================================

private fun migrateRecords(
records: JSONArray
) {

for (
i in 0 until records.length()
) {

try {

val obj =
records.getJSONObject(i)

val existingMatches =
obj.optJSONArray(
"matches"
)

if (
existingMatches != null
) {
continue
}

val odds =
obj.optJSONArray(
"odds"
)

if (
odds == null ||
odds.length() == 0
) {
continue
}

val bankroll =
obj.optDouble(
"bankroll",
0.0
)

var reciprocal = 0.0

for (
oddIndex in 0 until odds.length()
) {

val odd =
odds.optDouble(
oddIndex,
0.0
)

if (
odd > 0.0
) {
reciprocal +=
1.0 / odd
}
}

val matches =
JSONArray()

for (
matchIndex in 0 until odds.length()
) {

val odd =
odds.optDouble(
matchIndex,
0.0
)

if (
odd <= 0.0
) {
continue
}

val stake =
if (
reciprocal > 0.0
) {
bankroll /
(odd * reciprocal)
} else {
0.0
}

val payout =
stake * odd

val match =
JSONObject()

match.put(
"number",
matchIndex + 1
)

match.put(
"odd",
odd
)

match.put(
"stake",
stake
)

match.put(
"payout",
payout
)

match.put(
"status",
"BEKLİYOR"
)

matches.put(match)
}

obj.put(
"matches",
matches
)

// Eski toplam durumunu artık kullanmıyoruz.
obj.put(
"status",
"BEKLİYOR"
)

} catch (_: Exception) {
}
}
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

var totalMatches = 0
var wonMatches = 0
var lostMatches = 0
var waitingMatches = 0

var totalInvestment = 0.0
var totalPayout = 0.0
var realizedProfit = 0.0

var highestPayout = 0.0
var highestProfit =
Double.NEGATIVE_INFINITY

for (
couponIndex in 0 until records.length()
) {

val coupon =
records.getJSONObject(
couponIndex
)

val matches =
getMatchesForRecord(
coupon
)

for (
matchIndex in 0 until matches.length()
) {

val match =
matches.getJSONObject(
matchIndex
)

val stake =
match.optDouble(
"stake",
0.0
)

val payout =
match.optDouble(
"payout",
0.0
)

val status =
match.optString(
"status",
"BEKLİYOR"
)

totalMatches++

totalInvestment +=
stake

when (status) {

"KAZANDI" -> {

wonMatches++

totalPayout +=
payout

realizedProfit +=
payout - stake
}

"KAYBETTİ" -> {

lostMatches++

realizedProfit -=
stake
}

else -> {

waitingMatches++
}
}

if (
payout > highestPayout
) {
highestPayout =
payout
}

val matchProfit =
when (status) {

"KAZANDI" ->
payout - stake

"KAYBETTİ" ->
-stake

else ->
0.0
}

if (
status != "BEKLİYOR" &&
matchProfit > highestProfit
) {
highestProfit =
matchProfit
}
}
}

val completed =
wonMatches + lostMatches

val winRate =
if (
completed > 0
) {

wonMatches.toDouble() /
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

// =====================================================
// BANKROLL
// =====================================================

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

// =====================================================
// MAÇ İSTATİSTİKLERİ
// =====================================================

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
"📊 MAÇ İSTATİSTİKLERİ",
17f,
true,
darkBlueColor
)
)

stats.addView(
makeText(
"🎯 Toplam maç: $totalMatches",
15f,
true
)
)

stats.addView(
makeText(
"🟢 Kazandı: $wonMatches",
14f,
true,
greenColor
)
)

stats.addView(
makeText(
"🔴 Kaybetti: $lostMatches",
14f,
true,
redColor
)
)

stats.addView(
makeText(
"🟠 Bekliyor: $waitingMatches",
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
"📈 Kazananlardan geri dönüş: %.2f TL",
totalPayout
),
14f
)
)

val profitText =
if (
realizedProfit >= 0.0
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
realizedProfit >= 0.0
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

// =====================================================
// BANKROLL AYARI
// =====================================================

val bankrollButton =
Button(this).apply {

text =
"💰 BAŞLANGIÇ BANKROLL AYARLA"

isAllCaps = false

textSize = 14f

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
)
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
// MAÇLARI GETİR
// =========================================================

private fun getMatchesForRecord(
record: JSONObject
): JSONArray {

val existing =
record.optJSONArray(
"matches"
)

if (
existing != null
) {
return existing
}

// Güvenlik amaçlı eski kayıt dönüşümü
val odds =
record.optJSONArray(
"odds"
)

val result =
JSONArray()

if (
odds == null
) {
return result
}

val bankroll =
record.optDouble(
"bankroll",
0.0
)

var reciprocal = 0.0

for (
i in 0 until odds.length()
) {

val odd =
odds.optDouble(
i,
0.0
)

if (
odd > 0.0
) {
reciprocal +=
1.0 / odd
}
}

for (
i in 0 until odds.length()
) {

val odd =
odds.optDouble(
i,
0.0
)

if (
odd <= 0.0
) {
continue
}

val stake =
if (
reciprocal > 0.0
) {
bankroll /
(odd * reciprocal)
} else {
0.0
}

val match =
JSONObject()

match.put(
"number",
i + 1
)

match.put(
"odd",
odd
)

match.put(
"stake",
stake
)

match.put(
"payout",
stake * odd
)

match.put(
"status",
"BEKLİYOR"
)

result.put(match)
}

record.put(
"matches",
result
)

saveRecords(
getRecordsWithoutMigration()
)

return result
}

// =========================================================
// BANKROLL DİYALOĞU
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
dp(14),
dp(6),
dp(14),
dp(6)
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
records.getJSONObject(
index
)

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
dp(500)
)
)

val clearButton =
Button(this).apply {

text =
"🗑 TÜM KUPONLARI SİL"

isAllCaps = false

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
couponIndex: Int,
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

val matches =
getMatchesForRecord(
obj
)

val card =
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
Color.WHITE,
16f,
Color.rgb(
225,
229,
235
)
)
)
}

// =====================================================
// BAŞLIK
// =====================================================

card.addView(
makeText(
"🎫 $name",
18f,
true,
darkBlueColor
)
)

card.addView(
makeText(
"📅 $date • ⚽ $matchCount maç",
13f,
false,
grayTextColor
)
)

// =====================================================
// MAÇLAR
// =====================================================

for (
matchIndex in 0 until matches.length()
) {

val match =
matches.getJSONObject(
matchIndex
)

addMatchStatusCard(
card,
records,
couponIndex,
matchIndex,
match
)
}

// =====================================================
// KUPON SİL
// =====================================================

val delete =
Button(this).apply {

text =
"🗑 KUPONU SİL"

isAllCaps = false

textSize = 13f

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
couponIndex,
name
)
}
}

card.addView(
delete,
LinearLayout.LayoutParams(
-1,
dp(44)
).apply {

setMargins(
0,
dp(10),
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
dp(7)
)
}
)
}

// =========================================================
// TEK MAÇ DURUM KARTI
// =========================================================

private fun addMatchStatusCard(
parent: LinearLayout,
records: JSONArray,
couponIndex: Int,
matchIndex: Int,
match: JSONObject
) {

val number =
match.optInt(
"number",
matchIndex + 1
)

val odd =
match.optDouble(
"odd",
0.0
)

val stake =
match.optDouble(
"stake",
0.0
)

val payout =
match.optDouble(
"payout",
0.0
)

val status =
match.optString(
"status",
"BEKLİYOR"
)

val card =
LinearLayout(this).apply {

orientation =
LinearLayout.VERTICAL

setPadding(
dp(10),
dp(9),
dp(10),
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
statusColor(status)
)
)
}

// Üst satır
val top =
LinearLayout(this).apply {

orientation =
LinearLayout.HORIZONTAL

gravity =
Gravity.CENTER_VERTICAL
}

top.addView(
makeText(
"Maç $number",
15f,
true
),
LinearLayout.LayoutParams(
0,
LinearLayout.LayoutParams.WRAP_CONTENT,
1f
)
)

top.addView(
makeText(
String.format(
Locale.US,
"Oran %.2f",
odd
),
14f,
true,
darkBlueColor
)
)

card.addView(top)

card.addView(
makeText(
String.format(
Locale.US,
"Yatırım: %.2f TL • Gelirse: %.2f TL",
stake,
payout
),
13f
)
)

card.addView(
makeText(
"Durum: $status",
13f,
true,
statusColor(status)
)
)

// =====================================================
// DURUM BUTONLARI
// =====================================================

val buttons =
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

setMatchStatus(
records,
couponIndex,
matchIndex,
"BEKLİYOR"
)
}

val won =
smallStatusButton(
"🟢 Kazandı",
greenColor
)

won.setOnClickListener {

setMatchStatus(
records,
couponIndex,
matchIndex,
"KAZANDI"
)
}

val lost =
smallStatusButton(
"🔴 Kaybetti",
redColor
)

lost.setOnClickListener {

setMatchStatus(
records,
couponIndex,
matchIndex,
"KAYBETTİ"
)
}

buttons.addView(
waiting,
LinearLayout.LayoutParams(
0,
dp(42),
1f
).apply {

setMargins(
0,
dp(7),
dp(3),
0
)
}
)

buttons.addView(
won,
LinearLayout.LayoutParams(
0,
dp(42),
1f
).apply {

setMargins(
dp(3),
dp(7),
dp(3),
0
)
}
)

buttons.addView(
lost,
LinearLayout.LayoutParams(
0,
dp(42),
1f
).apply {

setMargins(
dp(3),
dp(7),
0,
0
)
}
)

card.addView(buttons)

parent.addView(
card,
LinearLayout.LayoutParams(
-1,
LinearLayout.LayoutParams.WRAP_CONTENT
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

// =========================================================
// DURUM RENGİ
// =========================================================

private fun statusColor(
status: String
): Int {

return when (status) {

"KAZANDI" ->
greenColor

"KAYBETTİ" ->
redColor

else ->
orangeColor
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
// TEK MAÇ DURUMU DEĞİŞTİR
// =========================================================

private fun setMatchStatus(
records: JSONArray,
couponIndex: Int,
matchIndex: Int,
status: String
) {

try {

val coupon =
records.getJSONObject(
couponIndex
)

val matches =
getMatchesForRecord(
coupon
)

if (
matchIndex < 0 ||
matchIndex >= matches.length()
) {
return
}

val match =
matches.getJSONObject(
matchIndex
)

match.put(
"status",
status
)

coupon.put(
"matches",
matches
)

// Kuponun genel status alanı artık
// sadece bilgi amaçlı tutuluyor.
coupon.put(
"status",
calculateCouponStatus(
matches
)
)

saveRecords(
records
)

Toast.makeText(
this,
"Maç ${matchIndex + 1}: $status",
Toast.LENGTH_SHORT
).show()

// =================================================
// ÖNEMLİ:
// showHistory() ÇAĞRILMIYOR.
// Böylece ikinci AlertDialog açılmıyor.
// =================================================

refreshHistoryDialog()

} catch (_: Exception) {

Toast.makeText(
this,
"Maç durumu güncellenemedi.",
Toast.LENGTH_SHORT
).show()
}
}

// =========================================================
// KUPON GENEL DURUMU
// =========================================================

private fun calculateCouponStatus(
matches: JSONArray
): String {

var won = 0
var lost = 0
var waiting = 0

for (
i in 0 until matches.length()
) {

val status =
matches
.getJSONObject(i)
.optString(
"status",
"BEKLİYOR"
)

when (status) {

"KAZANDI" ->
won++

"KAYBETTİ" ->
lost++

else ->
waiting++
}
}

return when {

lost > 0 ->
"KAYBETTİ"

waiting > 0 ->
"BEKLİYOR"

won > 0 ->
"KAZANDI"

else ->
"BEKLİYOR"
}
}

// =========================================================
// AKTİF KUPON YÖNETİMİ DİYALOĞU
// =========================================================

private var historyDialog: AlertDialog? = null

private fun refreshHistoryDialog() {

// Eski pencereyi kapat.
// Yeni pencereyi ÜSTÜNE açmıyoruz.
historyDialog?.dismiss()

historyDialog = null

// Tek pencere olarak güncel halini açıyoruz.
showHistory()
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

historyDialog?.dismiss()

historyDialog = null

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

historyDialog?.dismiss()

historyDialog = null
}
.show()
}

// =========================================================
// KAYITLARI OKU - YAN ETKİSİZ
// =========================================================

private fun getRecordsWithoutMigration(): JSONArray {

val saved =
prefs.getString(
"records_json",
null
)

if (
!saved.isNullOrEmpty()
) {

try {
return JSONArray(saved)
} catch (_: Exception) {
}
}

return JSONArray()
}
}
