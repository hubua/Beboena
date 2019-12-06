# Beboena


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

- Build a Responsive UI with ConstraintLayout   https://developer.android.com/training/constraint-layout/

- Support different screen sizes                https://developer.android.com/training/multiscreen/screensizes
- Building a Responsive UI in Android           https://medium.com/androiddevelopers/building-a-responsive-ui-in-android-7dc7e4efcbb3

#### UI notifications
- https://developer.android.com/training/snackbar/showing
- https://developer.android.com/guide/topics/ui/notifiers/toasts
- https://github.com/GrenderG/Toasty
- https://github.com/Muddz/StyleableToast

### Data

- Data storage                                  https://developer.android.com/guide/topics/data/data-storage
- Auto backup                                   https://developer.android.com/guide/topics/data/autobackup

### Settings

- Settings screen                               https://developer.android.com/guide/topics/ui/settings.html


Kotlin
------------------------------
- https://try.kotlinlang.org/
- http://kotlinlang.org/docs/reference/
- https://kotlinlang.org/docs/reference/coding-conventions.html
- https://developer.android.com/kotlin/style-guide


Unit tests
------------------------------
- https://medium.com/@yair.kukielka/android-unit-tests-explained-219b04dc55b5
- https://medium.com/@yair.kukielka/android-unit-tests-explained-part-2-a0f1e1413569


Emulator
------------------------------
- HAXM                                          https://github.com/intel/haxm/blob/master/docs/manual-windows.md
- SDP                                           https://github.com/intuit/sdp

`bcdedit /set hypervisorlaunchtype off`
`bcdedit /set hypervisorlaunchtype auto`

select deployment target / same selection


Features todo
==============================

- დამატებითი სიტყვები და წინადადებები.
- ფიქსირებული ზომის ქვე-სიმრავლის შემთხვევითად არჩევა ასოს წინადადებების სრული სიმრავლიდან.
- წინადადების თარგმნისას, დაფიქსირება იმ ასოების რომლებშიც შეცდომა ხდება, და შეთავაზება ამ ასოების გადამეორების.


[Mastering Markdown](https://guides.github.com/features/mastering-markdown/)   