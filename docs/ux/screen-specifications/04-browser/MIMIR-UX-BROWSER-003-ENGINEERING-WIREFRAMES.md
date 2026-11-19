Mimir Browser — Engineering Wireframes

Additional Screens 18–21

المرجع: Mimir Browser UX Specification، Detailed Interface Architecture، وComplete Architecture, Contracts & Integration Blueprint.

النطاق: استكمال الشاشات الأربع التي لم تدخل ضمن المخططات الأساسية الـ17. لا تستبدل هذه الوثيقة مواصفات Browser السابقة، بل تكملها.

1. قواعد هندسية خاصة

• الهاتف هو المرجع الأول.
• جميع القياسات بـ dp، والنصوص بـ sp.
• هامش المحتوى الأساسي 16dp.
• الحد الأدنى لهدف اللمس 48×48dp.
• مساحة المحتوى قابلة للتمرير، بينما تثبت إجراءات الشاشة الأساسية.
• بيانات Incognito لا تظهر في History أو Sessions العادية.
• كل أداة Developer أو Extension تعمل ضمن صلاحيات Browser وKernel، ولا تحصل على صلاحية عامة لمجرد تثبيتها.

───

Screen 18 — Private Browsing / Incognito

الهدف: إنشاء وإدارة جلسات خاصة مع عزل واضح وقابل للتحقق عن ملفات التصفح العادية.

مخطط الهاتف

┌────────────────────────────────┐
│ Safe Area │
├────────────────────────────────┤
│ ← Private Browsing ⋮ │
├────────────────────────────────┤
│ │
│ PRIVATE SESSION │
│ │
│ Privacy Status │
│ Session Isolation Indicator │
│ │
│ ┌────────────────────────────┐ │
│ │ Search or enter address │ │
│ └────────────────────────────┘ │
│ │
│ Private Tabs │
│ ┌────────────────────────────┐ │
│ │ Tab Preview │ │
│ │ Page Title Close │ │
│ └────────────────────────────┘ │
│ │
│ Privacy Information │
│ • Data isolation status │
│ • Session-only permissions │
│ • Storage behavior │
│ │
├────────────────────────────────┤
│ + New Private Tab Close All │
└────────────────────────────────┘

هندسة المكونات

المكون| السلوك
Private Session Header| يوضح أن المستخدم داخل جلسة خاصة
Isolation Indicator| يعرض حالة العزل الفعلية، لا مجرد رمز بصري
Address/Search| يتعامل مع URL والبحث
Private Tab List| يعرض تبويبات الجلسة الحالية فقط
Privacy Information| يوضح البيانات التي لا تحفظ والقيود الفعلية
Close All| يغلق الجلسة ويبدأ إجراءات التنظيف

حالات الشاشة

• No Private Session: لا توجد جلسة خاصة نشطة.
• Creating Session: إنشاء سياق معزول.
• Active: الجلسة جاهزة.
• Cleanup Pending: إغلاق الجلسة والتنظيف قيد التنفيذ.
• Cleanup Failed: فشل تنظيف بعض الموارد؛ يجب إظهار الحالة وعدم الادعاء بأن العزل انتهى.
• Restricted: وظيفة غير متاحة على المنصة أو Runtime الحالي.

التفاعلات

1. New Private Session ينشئ Session ID منفصلًا عن Profile العادي.
2. New Private Tab ينشئ تبويبًا ضمن الجلسة نفسها.
3. فتح موقع لا يضيفه إلى History العادي.
4. إغلاق تبويب لا ينهي الجلسة إن بقيت تبويبات أخرى.
5. Close All يطلب تأكيدًا إذا كانت هناك عمليات نشطة.
6. عند إغلاق الجلسة، تُنهى صلاحياتها المؤقتة وتُزال بياناتها وفق سياسة التخزين.
7. عند فشل التنظيف، تعرض Diagnostics وواجهة الجلسة النتيجة الفعلية.

ضوابط أمنية إلزامية

• لا مشاركة تلقائية للذاكرة العادية أو Context العادي.
• لا مزامنة تلقائية للتبويبات الخاصة.
• لا تسجيل History عادي.
• لا تمرير محتوى الصفحة إلى نموذج إلا بعد توضيح نطاق المشاركة والحصول على الإذن المطلوب.
• لا تخزين أسرار أو كلمات مرور في سياق النموذج.
• لا يجوز وصف Incognito بأنه يخفي النشاط عن المواقع أو مزود الشبكة؛ يجب وصف حدود الخصوصية بدقة.
• يجب أن يكون العزل على مستوى Runtime والتخزين والجلسات، وليس مجرد Theme مختلف.

التكيف مع الأجهزة

• Tablet: قائمة التبويبات في جانب، ومحتوى الصفحة في الجانب الآخر.
• Desktop: نافذة أو Profile خاص منفصل بصريًا، مع إظهار مؤشر الخصوصية في شريط العنوان.
• Mobile: الجلسة الخاصة مساحة مستقلة، مع إجراء واضح للخروج منها.

───

Screen 19 — Extensions Manager

الهدف: إدارة الإضافات وتثبيتها وتحديثها وتعطيلها ومراجعة صلاحياتها.

مخطط الهاتف

┌────────────────────────────────┐
│ ← Extensions + │
├────────────────────────────────┤
│ Search Extensions │
│ │
│ [Installed] [Available] │
│ │
│ ┌────────────────────────────┐ │
│ │ Extension Icon │ │
│ │ Extension Name │ │
│ │ Version / Publisher │ │
│ │ Enabled Toggle │ │
│ │ Permissions Summary │ │
│ │ Details ⋮ │ │
│ └────────────────────────────┘ │
│ │
│ ... │
├────────────────────────────────┤
│ Extension Security │
└────────────────────────────────┘

صفحة تفاصيل الإضافة

┌────────────────────────────────┐
│ ← Extension Details ⋮ │
├────────────────────────────────┤
│ Icon / Name / Version │
│ Publisher / Verification │
│ │
│ Description │
│ │
│ Permissions │
│ • Read page content │
│ • Access selected sites │
│ • Modify page content │
│ │
│ Site Access │
│ All / Selected / On Click │
│ │
│ Runtime Status │
│ Errors / Last Activity │
├────────────────────────────────┤
│ Disable / Remove / Update │
└────────────────────────────────┘

المكونات

• Extension Card.
• Extension Identity.
• Publisher / Signature Status.
• Version.
• Enabled Toggle.
• Permission Summary.
• Site Access Scope.
• Runtime Health.
• Update Status.
• Error Details.

التفاعلات

• Install: يبدأ عملية تثبيت خاضعة للتحقق.
• Enable: يتحقق من الأذونات المطلوبة قبل التفعيل.
• Disable: يوقف الإضافة دون حذف بياناتها إلا إذا نصت السياسة على ذلك.
• Update: يعرض التغييرات والصلاحيات الجديدة قبل اعتمادها.
• Remove: يوضح البيانات التي ستُحذف والبيانات التي ستبقى.
• Details: يفتح تفاصيل الإضافة.
• Site Access: يحدد المواقع المسموح بها.

حالات الشاشة

• Not Installed.
• Installing.
• Installed / Disabled.
• Enabled.
• Update Available.
• Permission Review Required.
• Blocked by Policy.
• Runtime Error.
• Unsupported Platform.
• Unverified Publisher.

ضوابط التكامل

• Extensions Runtime Adapter مستقل وقابل للاستبدال.
• لا يُسمح لإضافة بتجاوز Kernel Policy.
• لا تمنح إضافة صلاحيات Browser AI تلقائيًا.
• صلاحيات قراءة الصفحات وتعديلها منفصلة.
• صلاحيات كل موقع محددة النطاق.
• لا تُعرض إضافة على أنها آمنة لمجرد نجاح التثبيت.
• يجب تسجيل عمليات التثبيت والتحديث وتغيير الصلاحيات في Audit.

التكيف

• Tablet: قائمة إضافات مع لوحة تفاصيل.
• Desktop: قائمة جانبية، تفاصيل في Workspace، ولوحة صلاحيات مستقلة.
• Mobile: قائمة ثم صفحة تفاصيل منفصلة.

───

Screen 20 — Developer Tools

الهدف: توفير أدوات فحص وتشخيص متقدمة للصفحات، مع إبقاء وظائفها منفصلة عن صلاحيات التحكم في المتصفح.

مخطط الهاتف

┌────────────────────────────────┐
│ ← Developer Tools ⋮ │
├────────────────────────────────┤
│ Current Tab / Domain │
│ Runtime Status │
│ │
│ [Elements] [Console] │
│ [Network] [Storage] │
│ │
│ ┌────────────────────────────┐ │
│ │ Selected Tool Panel │ │
│ │ │ │
│ │ Inspect / Logs / Requests │ │
│ │ │ │
│ │ Results │ │
│ └────────────────────────────┘ │
│ │
│ Selected Element / Request │
│ Details │
├────────────────────────────────┤
│ Refresh Clear More │
└────────────────────────────────┘

الأقسام

Elements

• DOM Tree.
• Element Selection.
• Attributes.
• Computed Properties عند دعم Runtime.
• Accessibility Properties.
• Highlight in Page.
• Read-only افتراضيًا.

Console

• Runtime Messages.
• Errors.
• Warnings.
• Logs.
• Filter by Severity.
• Clear Console.
• Command Execution إذا كانت المنصة تدعمه، ضمن قيود أمان مستقلة.

Network

• Request List.
• Method / Domain / Status.
• Timing.
• Request/Response Metadata.
• Failure Details.
• Sensitive Headers and Values Redacted by Default.

Storage

• Cookies.
• Local Storage.
• Session Storage.
• IndexedDB عند دعمها.
• Storage Usage.
• Site-scoped Clearing.
• لا تعرض قيم الأسرار تلقائيًا.

التفاعلات

• اختيار تبويب من أدوات المطور يربط الفحص بالتبويب.
• اختيار Element يحدد مرجعه ويتيح إبرازه في الصفحة.
• تحديث الصفحة يبطل مراجع DOM السابقة.
• اختيار Network Request يعرض تفاصيلها.
• Clear Console يمسح عرض السجل، ولا يغير بيانات الموقع.
• Clear Site Storage عملية منفصلة وتتطلب تأكيدًا.

الحالات

• No Active Tab.
• Attaching DevTools.
• Connected.
• Detached.
• Runtime Unsupported.
• Inspection Unavailable.
• Data Redacted.
• Permission Required.
• Runtime Crashed.

ضوابط الأمان

• Developer Tools لا تمنح وصولًا عامًا إلى بيانات ملفات المستخدم.
• لا تعرض كلمات المرور أو Tokens أو Cookies الحساسة بصورتها الخام.
• تنفيذ أوامر Console يخضع لسياسات منفصلة عن قراءة Console.
• لا يمكن استخدام DevTools لتجاوز صلاحيات Browser AI.
• جميع عمليات تعديل DOM أو Storage تسجل عند الحاجة إلى التدقيق.

التكيف

• Mobile: Tabs داخلية، ولوحة تفاصيل أسفل النتائج.
• Tablet: أدوات في عمود، ومحتوى النتائج في عمود ثانٍ.
• Desktop: DevTools Docked أو Separate Panel، مع دعم اختصارات لوحة المفاتيح.

───

Screen 21 — Browser Diagnostics

الهدف: إظهار صحة المحرك والـ Runtime والـ Adapters والقدرات والعمليات الفاشلة، دون إخفاء الأعطال.

مخطط الهاتف

┌────────────────────────────────┐
│ ← Browser Diagnostics Refresh│
├────────────────────────────────┤
│ Overall Browser Health │
│ Status / Last Checked │
│ │
│ Runtime │
│ Adapter / Version / Health │
│ │
│ Capabilities │
│ Navigation Status │
│ Tabs Status │
│ Page Observe Status │
│ Downloads Status │
│ AI Controller Status │
│ │
│ Active Operations │
│ Operation ID / State │
│ │
│ Recent Errors │
│ Error Code / Time / Details │
│ │
│ Diagnostics Reports │
└────────────────────────────────┘

المكونات

• Overall Health.
• Runtime Adapter Status.
• Platform Capabilities.
• Engine Lifecycle.
• Active Operations.
• Pending Approvals.
• Recent Errors.
• Recovery State.
• Diagnostics Export.
• Support Bundle.

التفاعلات

• Refresh يعيد فحص الحالة من Runtime.
• فتح Capability يعرض حالة توافرها وسبب عدم التوافر.
• فتح Error يعرض Error Code وCorrelation ID والتفاصيل الآمنة.
• Export Diagnostics ينشئ تقريرًا بعد تنقية الأسرار.
• Retry لا يظهر إلا للعمليات القابلة لإعادة المحاولة.
• Restart Runtime إجراء منفصل يتطلب توضيح أثره على التبويبات والمهام.

قواعد صحة الحالة

• لا تستخدم نسبة صحة تقديرية دون تعريف مصدرها.
• لا تعتبر وجود Adapter دليلًا على جاهزية جميع قدراته.
• كل Capability تعرض إحدى الحالات المعتمدة: Available، Unavailable، Blocked، Not Ready، Failed، Unsupported.
• إذا كان Runtime غير متصل، لا تعرض وظائفه على أنها تعمل.
• لا تُخفى Noop/Stubs خلف حالة Ready.

التكامل

• Diagnostics يقرأ Engine Health وKernel Capability Resolution.
• لا يغير سياسات Kernel.
• لا ينفذ إصلاحات تلقائية حساسة.
• أي Recovery يمر عبر Lifecycle Contract.
• يحتفظ كل تقرير بـ Timestamp وRuntime Version وCorrelation ID.

───

7. روابط الشاشات الأربع

المصدر| الإجراء| الوجهة
Browser Workspace| Private Browsing| Incognito
Browser Settings| Extensions| Extensions Manager
Browser Workspace| Developer Tools| Developer Tools
Browser Settings| Diagnostics| Browser Diagnostics
Extensions Manager| Extension Error| Browser Diagnostics
Developer Tools| Runtime Failure| Browser Diagnostics
Incognito| Close Session| Browser Home / Previous Context
Diagnostics| Runtime Restart| Browser Workspace بعد استعادة الحالة

8. شروط القبول

• لا تُعامل Incognito كـ Profile عادي.
• لا تُفعّل Extension دون تحقق من الصلاحيات.
• لا تمنح Developer Tools صلاحيات غير محدودة.
• لا تعرض Diagnostics حالة صحة غير مثبتة.
• كل شاشة تدعم RTL/LTR.
• كل شاشة تدعم حالات Loading/Empty/Error/Unavailable.
• كل عملية لها معرف وتتبع ونتيجة فعلية.
• كل الانتقالات تحافظ على معرفات التبويبات والمهام.
• لا توجد أزرار شكلية بلا تنفيذ.
• لا يُعلن اكتمال الشاشات الأربع قبل اختبارات التفاعل والأمان والتكيف.

ملاحظة: هذه مخططات هندسية وصفية تنفيذية، وليست إثباتًا أن الإضافات أو Developer Tools أو Incognito Runtime موجودة بالفعل في المستودع.
