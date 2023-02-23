# Beboena

ბებოენა - ისწავლეთ ხუცური

"ბებო ენა" გეხმარებათ ისწავლოთ ხუცური ანბანი და წაიკითხოთ ძველ-ქართულად

ისწავლეთ ძველი ქართული ხუცური (ნუსხური და ასომთავრული) ანბანი მსგავსად იმისა, როგორც სწავლობენ თანამედროვე ანბანს "დედა ენით" — სწავლის თითოეულ ნაბიჯზე თქვენ გეძლევათ ახალი ასო-ბგერა, და უკვე შესწავლილი ასოებისგან ხდება სიტყვების და წინადადებების შედგენა.

"ბებო ენა" გიწერთ სიტყვას ან წინადადებას ხუცურად, ხოლო თქვენ უნდა მიუთითოთ მისი შესაბამისი თარგმანი თანამედროვე ქართულად.
ყოველი ასოს შესწავლის შემდეგ "ბებო ენა" გაძლევთ შეფასებას, და რეკომნდაციას — გააგრძელოთ შემდეგ თუ ხელახლა გადაიმეოროთ მასალა.

<b>"ბებო ენა" - ეს არის ხუცურის შესწავლის საინტერესო და ეფექტური საშუალება.</b>
<h1>ისწავლეთ ძველი ქართული ჩვენთან ერთად!</h1>

## Project structure

- `SlidingTabStrip` draws the strip of letters
- `SlidingTabLayout` displays the strip and bounds to pager adapters
- `LettersPagerAdapter` contains and renders the layout for letter details

- `activity_main.xml (MainActivity)` contains `nav_host_fragment (NavHostFragment)` with `fragment_home_letters.xml (LettersHomeFragment)` as Home.
- `fragment_home_letters.xml` contains `sliding_tab_layout (SlidingTabLayout)` and `view_pager (ViewPager)` to display list of letters.
- In the `LettersHomeFragment`, the `LettersPagerAdapter` is assigned to `view_pager` and then `SlidingTabLayout` uses it.

## Android documentation

### Activities and Fragments

- Introduction to Activities                    https://developer.android.com/guide/components/activities/intro-activities
- Fragments                                     https://developer.android.com/guide/components/fragments
- Communicate with other fragments              https://developer.android.com/training/basics/fragments/communicating

### Navigation

- Get started with the Navigation component     https://developer.android.com/guide/navigation/navigation-getting-started
- Pass data between destinations                https://developer.android.com/guide/navigation/navigation-pass-data
- Navigating between fragments                  https://www.youtube.com/watch?v=0yH1qDxqZMo
- Navigation drawer from scratch                https://codinginflow.com/tutorials/android/navigation-drawer/part-1-menu-activity-theme

### UI

- Material Design for Android                   https://developer.android.com/guide/topics/ui/look-and-feel/
- Design app themes with Theme Editor           https://developer.android.com/studio/write/theme-editor
- Material design icons                         https://material.io/resources/icons/

- Build a Responsive UI with ConstraintLayout   https://developer.android.com/training/constraint-layout/

- Support different screen sizes                https://developer.android.com/training/multiscreen/screensizes
- Building a Responsive UI in Android           https://medium.com/androiddevelopers/building-a-responsive-ui-in-android-7dc7e4efcbb3

- Menus                                         https://developer.android.com/guide/topics/ui/menus
- Dialogs                                       https://developer.android.com/guide/topics/ui/dialogs

- Settings                                      https://developer.android.com/guide/topics/ui/settings.html

#### UI notifications

- https://developer.android.com/training/snackbar/showing
- https://developer.android.com/guide/topics/ui/notifiers/toasts
- https://github.com/GrenderG/Toasty
- https://github.com/Muddz/StyleableToast

#### Rate

- https://github.com/kobakei/Android-RateThisApp
- https://github.com/hotchemi/Android-Rate

#### Sounds

- https://developer.android.com/guide/topics/media/mediaplayer
- https://developers.google.com/assistant/tools/sound-library

### Data

- Data storage      https://developer.android.com/guide/topics/data/data-storage
- Auto backup       https://developer.android.com/guide/topics/data/autobackup

- CSV               http://zetcode.com/articles/opencsv/

### Unit tests

- https://medium.com/@yair.kukielka/android-unit-tests-explained-219b04dc55b5
- https://medium.com/@yair.kukielka/android-unit-tests-explained-part-2-a0f1e1413569

### Emulator

- Use Windows Hypervisor Platform https://devblogs.microsoft.com/visualstudio/hyper-v-android-emulator-support/

- HAXM  https://github.com/intel/haxm/blob/master/docs/manual-windows.md
- SDP   https://github.com/intuit/sdp

`bcdedit /set hypervisorlaunchtype off`
`bcdedit /set hypervisorlaunchtype auto`

Enable wireless debugging in developer options
%LOCALAPPDATA%\Android\sdk\platform-tools
adb pair <PHONE_IP>:<PHONE_PORT>
adb connect <PHONE_IP>:<PHONE_PORT_PROVIDED_ABOVE_PAIR_WITH_QR_BUTTON>

## Kotlin

- https://try.kotlinlang.org/
- http://kotlinlang.org/docs/reference/
- https://kotlinlang.org/docs/reference/coding-conventions.html
- https://developer.android.com/kotlin/style-guide
- https://antonioleiva.com/listeners-several-functions-kotlin/

## Tools

- Fonts edited with FontForge-2019-08-01-Windows.exe

Publish
==============================

- Google Play Console                           https://play.google.com/apps/publish/
- Publish your app                              https://developer.android.com/studio/publish

- Feature graphic samples                       https://hotpot.ai/design/google_play_feature_graphic?id=WvhDptUJRNJi_1024_500

ASO
------------------------------

- https://www.apptamin.com/blog/optimize-play-store-app/
- https://support.google.com/googleplay/android-developer/answer/4448378

- https://www.mobiloud.com/blog/app-store-optimization/
- https://asostack.com/aso-keyword-optimization-in-practice-part-1-e43e8eb9a6f

Privacy Policies
------------------------------

- https://medium.com/@AndreSand/add-privacy-policy-page-to-your-android-application-54a7ea8f0fc8
- https://www.freeprivacypolicy.com/blog/privacy-policy-mobile-apps/
- https://getterms.io/app-privacy-policy


Features todo
==============================

- better result icons, ask help from artists
- Sync words with LOGA / Update LOGA
- აბა რა ასოა, მთავრული, დასამახსოვრებლად რთული, ერთმანეთთან მსგავსი
- გართლებული რეჟიმი როდესაც წინადადება რამდენიმე წამში ნელ ნელა ქრებაб ულიმიტო დრო, countdown for letters from middle
- ბოლო ორი სამი წინადადება სულ მთავრული
- არადამაკმაყოფილებელი ზღვრის გადაწევა ზემოთ, 50-ის მაგივრად 75%
- წინადადების თარგმნისას, დაფიქსირება იმ ასოების რომლებშიც შეცდომა ხდება, და შეთავაზება ამ ასოების გადამეორების.
- App content target audience children and families
- ტექსტები ნუსხურად როგორც პრაქტიკული საკითხავი
- App Quality Insights Firebase https://console.firebase.google.com/project/beboena-85f4d/overview?utm_source=studio

Known issues
------------------------------

java.lang.UnsupportedOperationException: at android.view.RenderNodeAnimator.pause
https://stackoverflow.com/questions/26749280/custom-circular-reveal-transition-results-in-java-lang-unsupportedoperationexce


[Mastering Markdown](https://guides.github.com/features/mastering-markdown/)
