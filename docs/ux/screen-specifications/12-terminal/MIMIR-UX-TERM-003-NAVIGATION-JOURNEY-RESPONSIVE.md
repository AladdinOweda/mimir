Mimir Terminal — Navigation, User Journey & Responsive Design Review

Document Title: Mimir Terminal — Navigation & User Journey Map + Responsive Design Review
Document Status: Comprehensive Design Draft — Pending Review and Registry Approval
Document ID: PENDING_DOCUMENT_REGISTRY
Scope: Terminal Navigation, User Journeys, Responsive Layout and Visual Design Review
Platforms: Android → Tablet → Desktop
Related Specification: Mimir Terminal — Screen-by-Screen UX & Interaction Specification
Visual Mockups: Not created
Implementation / Runtime Verification: Not performed

───

Part I — Navigation Architecture

1. Purpose

تحدد هذه الوثيقة كيف ينتقل المستخدم داخل Mimir Terminal، وكيف ترتبط الصفحات السبع عشرة ببعضها، وكيف تتغير طريقة التنقل والعرض بين الهاتف والتابلت وسطح المكتب.

الأهداف الأساسية:

1. الوصول السريع إلى جلسة Terminal دون تعقيد.
2. جعل تنفيذ الأوامر ومتابعة نتائجها واضحين.
3. ربط AI Assistant وAgents بسياق الجلسة دون تشتيت.
4. الوصول إلى البيئات والملفات والعمليات والسجلات من المكان المناسب.
5. المحافظة على سياق المستخدم عند الانتقال بين الصفحات.
6. استخدام خدمات Mimir المشتركة بدل بناء أنظمة موازية داخل Terminal.

2. Navigation Principles

2.1 ملكية التنقل

• Mimir Application Shell يملك التنقل العام.
• Terminal يملك التنقل الداخلي بين صفحاته.
• لا ينشئ Terminal Bottom Navigation عامًا خاصًا به.
• لا يكرر Terminal الـ Global Composer.
• لا ينشئ Terminal Approval Center أو Activity Center خاصًا به.
• الانتقال إلى Forge أو Files أو Secure Vault يتم من خلال نقاط ربط واضحة، وليس عن طريق دمج هذه المساحات داخل Terminal.

2.2 مستويات التنقل

المستوى| المسؤولية| مثال
Global Navigation| الانتقال بين مساحات Mimir| Home / Projects / Tasks / More
Terminal Entry| الدخول إلى Terminal| Terminal Home
Terminal Primary Navigation| الانتقال بين مناطق العمل الرئيسية| Sessions / Environments / History
Contextual Navigation| الوصول إلى تفاصيل العنصر الحالي| Session Details / Process Details
Cross-Space Navigation| الانتقال إلى مساحة أخرى| Forge / Files / Secure Vault
Command Palette| الوصول السريع إلى الصفحات والإجراءات| Open Session / Search Commands

2.3 قاعدة مهمة

لا يجب أن تظهر جميع صفحات Terminal كقائمة طويلة في كل مرة.

التنقل يتغير حسب السياق:

• المستخدم الذي يريد تشغيل أمر يصل مباشرة إلى Workspace.
• المستخدم الذي يريد متابعة جلسة يصل إلى Sessions.
• المستخدم الذي يريد مراجعة تنفيذ سابق يصل إلى History.
• المستخدم الذي يريد إعداد اتصال يصل إلى Connections & Hosts.
• المستخدم الذي يحتاج إلى تشخيص مشكلة يصل إلى Logs & Diagnostics.

───

Part II — Terminal Navigation Map

3. Global Entry and Navigation

المسار العام:

Mimir Application Shell → Terminal → Terminal Home

نقاط الدخول المقترحة داخل Shell:

• Drawer: مدخل Terminal الرئيسي.
• Projects أو مساحة المشروع: مدخل سياقي إلى Terminal المرتبط بالمشروع، إذا كانت العلاقة موجودة ومصرحًا بها.
• Tasks: فتح جلسة أو عملية مرتبطة بمهمة، عند وجود ارتباط فعلي.
• Command Palette العامة: فتح Terminal أو الوصول إلى جلسة موجودة.

لا ينبغي أن يؤدي فتح Terminal من مشروع إلى إنشاء جلسة جديدة تلقائيًا دون توضيح ذلك للمستخدم.

4. Primary Navigation Structure

الخريطة الأساسية للصفحات:

Mimir Application Shell
│
├── Global Navigation
│ ├── Home
│ ├── Projects
│ ├── Tasks
│ └── More
│
└── Terminal
│
├── T01 Terminal Home
│
├── Sessions
│ ├── T02 Sessions
│ ├── T03 Terminal Workspace
│ └── T04 Session Details
│
├── Commands
│ ├── T05 Command History
│ └── T06 Commands & Snippets
│
├── Environments
│ ├── T07 Environments
│ └── T08 Environment Details
│
├── Files & Paths
│ └── T09 File & Path Explorer
│
├── Processes
│ └── T10 Processes & Jobs
│
├── Connections
│ └── T11 Connections & Hosts
│
├── AI
│ ├── T12 AI Terminal Assistant
│ └── T13 Agents & Execution Runs
│
├── Security
│ └── T14 Permissions & Security
│
├── Diagnostics
│ └── T15 Logs & Diagnostics
│
├── Settings
│ └── T16 Terminal Settings
│
└── Quick Access
└── T17 Command Palette

هذه خريطة وظيفية وليست قائمة واجهة ثابتة. بعض العناصر تظهر كصفحات مستقلة، وبعضها يظهر كتفاصيل أو لوحات سياقية وفق حجم الشاشة.

5. Page-to-Page Navigation Matrix

الصفحة| الوصول الأساسي| الانتقالات المهمة
T01 Terminal Home| Shell → Terminal| Sessions, Environments, History, Connections, Settings
T02 Sessions| Home / Terminal Navigation| Workspace, Session Details
T03 Terminal Workspace| Sessions / Resume Session| Session Details, AI Assistant, Processes, Files, History
T04 Session Details| Sessions / Workspace| Workspace, Processes, Logs, Permissions
T05 Command History| Home / Commands| Workspace, Command Details, Snippets
T06 Commands & Snippets| Home / Commands| Workspace, History
T07 Environments| Home / Terminal Navigation| Environment Details, Workspace
T08 Environment Details| Environments| Workspace, Connections, Diagnostics
T09 File & Path Explorer| Workspace / Session Tools| Forge, Mimir Files, Workspace
T10 Processes & Jobs| Workspace / Session Details| Process Details, Logs, Session Details
T11 Connections & Hosts| Home / Environments| Environment Details, Security
T12 AI Terminal Assistant| Workspace / Contextual AI| Workspace, History, Agents
T13 Agents & Execution Runs| AI / Workspace| Run Details, Workspace, Logs, Tasks
T14 Permissions & Security| Settings / Protected Action| Global Approval Center, Security Details
T15 Logs & Diagnostics| Workspace / Processes / Settings| Session Details, Process Details
T16 Terminal Settings| Home / Settings| Environments, Security, Appearance
T17 Command Palette| Terminal Navigation / Shortcut| Any permitted Terminal destination

───

Part III — Navigation Behavior

6. Terminal Home

T01 هي نقطة البداية، ويجب أن تجيب سريعًا عن أربعة أسئلة:

1. هل لدي جلسة نشطة؟
2. هل يمكنني بدء جلسة جديدة؟
3. ما آخر ما عملت عليه؟
4. هل توجد مشكلة أو عملية تحتاج انتباهي؟

الأولوية البصرية:

1. Continue Session / New Session.
2. Active Sessions.
3. Recent Activity.
4. Quick Access to Environments and Commands.
5. Diagnostics أو Alerts عند وجودها.

لا ينبغي أن تتحول الصفحة إلى Dashboard مزدحم بكل أدوات Terminal.

7. Session Navigation

المسار الأساسي:

Terminal Home
↓
Sessions
↓
Select Session
↓
Terminal Workspace
↔
Session Details

قواعد التنقل:

• الضغط على جلسة نشطة يفتح Workspace ويستعيد سياقها.
• الضغط على معلومات الجلسة يفتح Session Details.
• العودة من Details تعيد المستخدم إلى نفس الجلسة ونفس موضعه.
• إغلاق شاشة Workspace لا يعني إيقاف الجلسة.
• مغادرة الصفحة لا تعني إلغاء عملية تعمل في الخلفية.
• إيقاف الجلسة أو العملية يحتاج إجراءً صريحًا وتوضيحًا للأثر.

8. Workspace Navigation

T03 هي مساحة التنفيذ الرئيسية.

يجب أن تبقى نافذة Terminal هي العنصر المركزي، بينما تظهر الأدوات المساندة عند الحاجة.

العناصر السياقية:

• Session information.
• Environment indicator.
• Command input / terminal interaction.
• Execution state.
• AI Assistant.
• Processes & Jobs.
• File & Path Explorer.
• Logs & Diagnostics.

على الهاتف، لا تُعرض كل هذه الأدوات في أعمدة صغيرة. تستخدم لوحات أو صفحات فرعية أو Bottom Sheets مناسبة.

على Desktop، يمكن توزيعها في Panels متجاورة.

9. Command Navigation

المسار:

Command History
↓
Select Command
↓
Review Command Details
↓
Open in Workspace
↓
Execute only after applicable checks

من Commands & Snippets:

Commands & Snippets
↓
Select Snippet
↓
Review / Edit
↓
Insert into Workspace

إدراج أمر لا يعني تنفيذه تلقائيًا.

يجب أن يظل المستخدم قادرًا على مراجعة الأمر قبل التشغيل، خصوصًا إذا كان يتضمن حذفًا أو تعديلًا أو اتصالًا خارجيًا أو صلاحيات مرتفعة.

10. Environment Navigation

Environments
↓
Environment Details
├── Capabilities
├── Configuration
├── Associated Sessions
├── Connections
└── Diagnostics

عند اختيار Environment من Workspace:

• يعرض النظام البيئة المرتبطة بالجلسة.
• لا يغير البيئة تلقائيًا لمجرد فتح تفاصيل بيئة أخرى.
• أي تغيير للبيئة الحالية يجب أن يكون صريحًا وواضح الأثر.

11. AI and Agent Navigation

المسار الأساسي:

Terminal Workspace
↓
AI Terminal Assistant
├── Explain
├── Diagnose
├── Suggest
├── Prepare Command
└── Request Execution
↓
Applicable Approval
↓
Execution
↓
Verification / Evidence

مسار Agents:

AI Terminal Assistant
↓
Agents & Execution Runs
↓
Select Run
↓
Run Details
├── Status
├── Steps
├── Tool Invocations
├── Output
├── Errors
└── Evidence

قواعد أساسية:

• AI Assistant لا يصبح Terminal Session.
• Agent Run ليس Process.
• Tool Invocation ليست Task.
• اقتراح AI ليس أمرًا منفذًا.
• بدء Agent Run لا يعني نجاح المهمة.
• نتائج التنفيذ تحتاج إلى حالة تحقق واضحة.
• صلاحيات Agent لا تتجاوز صلاحيات المستخدم أو سياسات النظام.

───

Part IV — Cross-Space Connections

12. Terminal and Forge

Terminal مستقل عن Forge، لكن يمكن أن يتكاملا.

أمثلة الانتقال:

• من Forge Project إلى Terminal Session مرتبطة بالمشروع.
• من Build & Test Center إلى Terminal لمراجعة مخرجات أمر بناء.
• من Terminal إلى Forge عند الحاجة إلى تعديل ملفات المشروع أو مراجعة تغييرات الكود.

القواعد:

• Forge يظل مالك تجربة دورة حياة هندسة البرمجيات.
• Terminal يظل مالك تجربة CLI والجلسات والأوامر والعمليات.
• لا تُنقل مسؤولية إدارة المشاريع أو Code Review إلى Terminal.
• لا يصبح Terminal محرر كود بديلًا لـ Forge.

13. Terminal and Mimir Files

من T09 File & Path Explorer:

• فتح الملف في Mimir Files عند الحاجة إلى إدارة الملف.
• فتح الملف في Forge عند الحاجة إلى تحريره ضمن مشروع برمجي.
• العودة إلى Terminal مع المحافظة على الجلسة والسياق.

لا ينبغي أن يؤدي مجرد عرض مسار ملف إلى منح صلاحية قراءة أو تعديل غير موجودة أصلًا.

14. Terminal and Secure Vault

عند الحاجة إلى بيانات اعتماد:

• لا تُعرض الأسرار كنص عادي داخل Terminal.
• لا تُحفظ كلمات المرور في Command History.
• لا تُضاف الأسرار إلى AI context افتراضيًا.
• يمكن طلب استخدام Secret عبر Secure Vault وفق التفويض المناسب.
• يجب إخفاء القيم الحساسة من Logs وDiagnostics وProvenance حيثما ينطبق.

الانتقال إلى Secure Vault لا يعني أن Terminal أصبح مالكًا للأسرار.

15. Shared Approval Center

عند وجود عملية تتطلب موافقة:

Terminal Action
↓
Policy & Permission Evaluation
↓
Global Approval Center
↓
User Decision
↓
Execution or Rejection
↓
Evidence and Activity Record

لا يُنشأ Approval Center داخل Terminal.

يجب أن توضح الموافقة، بحسب طبيعة العملية:

• العملية المطلوبة.
• الهدف أو المسار أو البيئة.
• الصلاحيات المطلوبة.
• التأثير المتوقع.
• إمكانية التراجع.
• البيانات التي قد تغادر الجهاز.
• هوية النموذج أو Agent المشارك عند الاقتضاء.

16. Shared Activity Trail

يمكن فتح سجل النشاط العام من Shell أو من نقطة سياقية داخل Terminal.

يجب التمييز بين:

• Session Activity.
• Command Execution.
• Process Events.
• Agent Runs.
• Tool Invocations.
• Approval Events.

هذه أحداث مختلفة، ويجب عدم دمجها في حالة واحدة مضللة.

───

Part V — User Journey Map

17. Journey A — First-Time Terminal Use

الهدف: بدء أول جلسة دون الحاجة إلى فهم كل إعدادات Terminal.

1. يفتح المستخدم Terminal من Shell.
2. يصل إلى Terminal Home.
3. يرى شرحًا مختصرًا لوظيفة Terminal.
4. يختار New Session.
5. يحدد Environment متاحة أو يختار البيئة الافتراضية إن كانت معرفة ومدعومة.
6. تظهر معلومات واضحة عن البيئة والصلاحيات.
7. يدخل Workspace.
8. يحصل على إرشاد سياقي بسيط عند الحاجة.

المتوقع:

• لا تظهر إعدادات متقدمة كلها مرة واحدة.
• لا يتم افتراض وجود Remote Host أو SSH أو Docker.
• لا يتم إنشاء اتصال غير مدعوم.
• لا يتم تنفيذ أمر تلقائيًا خلال onboarding.

18. Journey B — Start and Continue a Session

الهدف: فتح جلسة جديدة أو استكمال جلسة سابقة.

1. Terminal Home.
2. New Session أو Continue Session.
3. اختيار البيئة.
4. فتح Workspace.
5. تنفيذ الأوامر ومتابعة المخرجات.
6. مغادرة Workspace عند الحاجة.
7. العودة لاحقًا إلى الجلسة نفسها.

معايير النجاح:

• استعادة الجلسة وفق إمكانيات البيئة الفعلية.
• الاحتفاظ بحالة الواجهة والمسودة غير المنفذة.
• عدم اعتبار إغلاق الصفحة إيقافًا للجلسة.
• توضيح حالة الجلسة إذا تعذر استعادتها.

19. Journey C — Run a Command Safely

1. المستخدم يدخل أمرًا أو يختاره من Snippets.
2. النظام يعرض الأمر قبل التنفيذ عند الحاجة.
3. يتم فحص الصلاحيات والسياسات.
4. إذا تطلب الأمر موافقة، ينتقل المستخدم إلى Global Approval Center.
5. بعد الموافقة، يبدأ التنفيذ.
6. تظهر حالة العملية ومخرجاتها.
7. يتم التحقق من النتيجة قدر الإمكان.
8. يحفظ سجل التنفيذ ومصدره وبيانات Provenance المتاحة.

حالات التنفيذ التي يجب تمييزها:

• Draft.
• Ready.
• Awaiting Approval.
• Running.
• Succeeded.
• Failed.
• Cancelled.
• Timed Out.
• Interrupted.
• Verification Required.

لا يجوز عرض Succeeded لمجرد أن أداة التنفيذ أعادت استجابة دون تحقق كافٍ من النتيجة المطلوبة.

20. Journey D — Understand an Error with AI

1. يلاحظ المستخدم خطأً في Workspace.
2. يفتح AI Terminal Assistant.
3. يختار Explain أو Diagnose.
4. يحدد النظام السياق المتاح الذي يمكن استخدامه.
5. يعرض AI تفسيرًا ودرجة وضوح الأدلة والاقتراحات.
6. يراجع المستخدم الحل المقترح.
7. يمكن إدراج أمر مقترح في Workspace.
8. التنفيذ يخضع للفحوصات والموافقات المطلوبة.
9. يتم عرض النتيجة والتحقق منها.

مهم: لا يحق لـ AI اعتبار تفسيره حقيقة مؤكدة دون دليل من المخرجات أو البيئة.

21. Journey E — Agent Execution

1. يفتح المستخدم AI Assistant أو Agents & Execution Runs.
2. يختار Agent أو نمط تنفيذ متاحًا.
3. يحدد المهمة والهدف والنطاق.
4. تظهر خطة التنفيذ عند الحاجة.
5. تُراجع الصلاحيات والأفعال الحساسة.
6. يبدأ Run.
7. يتابع المستخدم الخطوات والحالة.
8. يمكن فتح Logs أو Tool Invocations.
9. تظهر النتيجة والأدلة.
10. يمكن العودة إلى Workspace أو Task المرتبطة.

لازم يظهر بوضوح:

• Agent Identity.
• Model / Provider إن توفرت البيانات.
• Local / Cloud / Hybrid Execution.
• Role and Contribution.
• Start / Completion Time.
• Run Status.
• Tool Invocations.
• Verification Evidence.

22. Journey F — Inspect a Process or Job

1. يفتح المستخدم Processes & Jobs من Workspace.
2. يختار Process.
3. تظهر الحالة والمدة والارتباط بالجلسة.
4. يفتح Logs & Diagnostics عند الحاجة.
5. يختار إجراءً مسموحًا مثل Stop إن كان مدعومًا.
6. تظهر نتيجة الإجراء بوضوح.

يجب التمييز بين إيقاف Process وإغلاق Session وإلغاء Agent Run.

23. Journey G — Inspect Files and Paths

1. يفتح File & Path Explorer.
2. يستعرض المسارات المتاحة.
3. يختار ملفًا.
4. يحدد الإجراء المناسب:
◦ فتح في Mimir Files.
◦ فتح في Forge.
◦ نسخ المسار.
◦ العودة إلى Workspace.
5. يتحقق النظام من الصلاحيات قبل أي عملية حساسة.

لا تُعرض عملية تعديل أو حذف على أنها متاحة إذا لم تدعمها البيئة أو الصلاحيات.

24. Journey H — Connect to an Environment or Host

1. يفتح Connections & Hosts.
2. يختار إضافة اتصال أو فحص اتصال موجود.
3. يحدد نوع الاتصال المدعوم.
4. يعرض النظام متطلبات الصلاحيات والبيانات المطلوبة.
5. يتم التعامل مع بيانات الاعتماد من خلال آليات آمنة.
6. يُنفذ اختبار الاتصال إذا كان مدعومًا.
7. تظهر نتيجة الاختبار بوضوح.
8. يمكن ربط الاتصال ببيئة أو جلسة وفق السياسات.

لا يُفترض وجود SSH أو Cloud أو Remote Execution لمجرد ظهور هذه الأنواع في التصميم.

25. Journey I — Diagnose a Terminal Problem

1. المستخدم يفتح Logs & Diagnostics.
2. يحدد نطاق الفحص: Session أو Process أو Environment.
3. تظهر النتائج المتاحة.
4. يتم تمييز البيانات الناقصة أو غير المتاحة.
5. يمكن طلب تفسير AI.
6. تظهر توصيات الإصلاح.
7. لا ينفذ النظام إصلاحًا مؤثرًا دون الصلاحيات والموافقات اللازمة.

26. Journey J — Change Terminal Settings

1. يفتح Terminal Settings.
2. يختار الإعداد المطلوب.
3. تظهر معاينة مباشرة حيثما ينطبق.
4. يحفظ التغيير.
5. يمكن استعادة القيمة الافتراضية.
6. لا تتغير إعدادات Mimir العامة من داخل Terminal إلا عبر إعدادات Shell أو Design System المملوكة لها.

───

Part VI — State Preservation and Back Behavior

27. State Preservation

يجب المحافظة على السياق عند الانتقال بين صفحات Terminal.

العناصر المطلوب استعادتها عندما تسمح البيئة بذلك:

• Active Session Reference.
• Selected Environment.
• Current Workspace Context.
• Scroll Position.
• Open Contextual Panel.
• Search Query.
• Selected History Item.
• Unsaved Draft.
• Selected Process or Run.
• Filters and Sorting.

يجب الفصل بين حفظ حالة الواجهة وحفظ حالة العملية الفعلية.

مثال: يمكن استعادة شاشة جلسة سابقة، لكن هذا لا يثبت أن العملية التي كانت تعمل فيها ما زالت نشطة.

28. Draft Preservation

• لا تُنفذ المسودة لمجرد الانتقال إلى صفحة أخرى.
• تحفظ المسودات وفق سياسة التخزين المعتمدة.
• لا تحفظ القيم السرية داخل المسودات العامة.
• يظهر تنبيه عند وجود تغيير غير محفوظ إذا كان فقدانه محتملًا.
• يمكن للمستخدم استعادة المسودة عند الرجوع.

29. Back Behavior

زر Back له معنى مرتبط بمستوى التنقل:

1. إغلاق لوحة سياقية مفتوحة.
2. العودة من صفحة تفاصيل إلى الصفحة التي فتحتها.
3. العودة من Workspace إلى Sessions دون إيقاف الجلسة.
4. العودة من Terminal إلى المساحة السابقة داخل Shell.

Back لا يعني:

• إيقاف Process.
• إلغاء Agent Run.
• حذف Session.
• إلغاء أمر تم تنفيذه.
• التراجع عن تغيير في البيئة.

الأفعال التي تحتاج إلغاءً أو إيقافًا يجب أن تمتلك تحكمًا صريحًا مستقلًا.

30. Background Work

عند مغادرة Terminal:

• لا يتوقف العمل تلقائيًا لمجرد مغادرة الصفحة.
• تظهر حالة العمليات المستمرة في أماكن المتابعة المناسبة.
• لا يوعد المستخدم باستمرار العمل في الخلفية إلا إذا كانت البيئة تدعم ذلك.
• عند فقدان الاتصال، يجب توضيح هل العملية مستمرة أو متوقفة أو غير معروفة.
• لا يُعرض نجاح غير مؤكد بعد انقطاع الاتصال.

───

Part VII — Responsive Visual Design Review

31. Android First — Primary Design Target

الأولوية الأولى في التصميم والمراجعة هي Android Mobile.

الهدف أن تكون تجربة Terminal احترافية وقوية، لكن قابلة للاستخدام على شاشة صغيرة دون تحويل الهاتف إلى Desktop مصغر.

31.1 Android Application Structure

الهيكل المقترح:

┌──────────────────────────────┐
│ Terminal Header │
│ Session / Environment Status │
├──────────────────────────────┤
│ │
│ │
│ Terminal Output │
│ │
│ │
│ │
├──────────────────────────────┤
│ Contextual Tools │
│ AI | Processes | Files | ... │
├──────────────────────────────┤
│ Global Composer │
├──────────────────────────────┤
│ Mimir Bottom Navigation │
└──────────────────────────────┘

هذا مخطط توزيع وظيفي، وليس تصميمًا نهائيًا بالبكسل.

31.2 Android Visual Priorities

A. Terminal Header

يحتوي على:

• اسم المساحة Terminal.
• اسم الجلسة الحالية.
• مؤشر البيئة.
• حالة الاتصال أو الجلسة.
• قائمة الإجراءات السياقية.

يجب أن يكون مختصرًا، ولا يحتل مساحة رأسية كبيرة.

B. Terminal Output

هو العنصر الأساسي بصريًا.

المتطلبات:

• مساحة عرض واسعة.
• خط واضح وقابل للقراءة.
• تمييز بصري بين الأمر والمخرجات والخطأ.
• دعم النسخ والتحديد.
• عدم الاعتماد على اللون وحده لتمييز الحالات.
• التعامل الجيد مع الأسطر الطويلة.
• تمرير مستقل عند الحاجة.
• دعم RTL في الواجهة المحيطة، مع الحفاظ على اتجاه النصوص والأوامر التقنية الصحيح.

C. Contextual Tools

تظهر الأدوات في شريط مختصر أو لوحة سياقية:

• AI.
• Processes.
• Files.
• History.
• Details.

لا ينبغي عرض كل الأدوات كأزرار كبيرة دائمة.

D. Global Composer

يبقى Composer العام مملوكًا لـ Application Shell.

في Terminal:

• يتكيف مع سياق الاستخدام.
• لا يتكرر كـ Composer ثانٍ.
• لا يغطي مخرجات Terminal.
• يحترم Safe Areas.
• يتكيف مع ظهور لوحة المفاتيح.
• يحافظ على المسودة.
• يسمح بالوصول إلى أدوات Terminal السياقية دون ازدحام.

يجب اختبار تفاعل Composer العام مع إدخال أوامر CLI بعناية، حتى لا يختلط إدخال الأوامر بمحادثة AI.

E. Bottom Navigation

يظل Bottom Navigation العام كما هو:

Home / Projects / Tasks / More

ولا يستبدله Terminal بشريط تنقل خاص به.

31.3 Android Navigation Pattern

• التنقل الأساسي عبر Shell.
• التنقل الداخلي عبر قوائم أو أشرطة سياقية.
• التفاصيل في صفحات أو لوحات مستقلة.
• Bottom Sheets للإجراءات الثانوية عندما تكون مناسبة.
• Command Palette للوصول السريع.
• لا تستخدم Split Panels ضيقة على الهاتف.

31.4 Android Typography and Spacing

• الحفاظ على وضوح النصوص التقنية.
• دعم تغيير حجم الخط العام.
• دعم Zoom مستقل لمحتوى الكود والأوامر داخل Terminal.
• عدم جعل Zoom داخل Terminal يغير خط بقية Mimir.
• عدم استخدام Glass Effects قوية خلف المخرجات النصية.
• الحفاظ على تباين مرتفع بين النص والخلفية.
• تجنب كثرة الظلال والطبقات الزخرفية.

31.5 Android Critical States

يجب تصميم حالات واضحة لكل من:

• No Sessions.
• Starting Session.
• Session Active.
• Session Disconnected.
• Session Interrupted.
• Running Command.
• Awaiting Approval.
• Command Failed.
• Process Stopped.
• Environment Unavailable.
• Permission Denied.
• Output Truncated.
• Reconnection Pending.

لا يكفي تصميم الحالة الطبيعية فقط.

───

32. Tablet — Expanded Workspace

الهدف في Tablet هو الاستفادة من المساحة الأكبر دون تغيير نموذج العمل الأساسي.

32.1 Tablet Layout

┌─────────────────────────────────────────────────────────────┐
│ Terminal Header / Session / Environment │
├──────────────────┬──────────────────────────────────────────┤
│ Terminal Tools │ │
│ │ Terminal Workspace │
│ Sessions │ │
│ Environments │ │
│ Processes │ │
│ History │ │
│ AI Assistant │ │
│ │ │
├──────────────────┴──────────────────────────────────────────┤
│ Global Composer │
├─────────────────────────────────────────────────────────────┤
│ Mimir Global Navigation │
└─────────────────────────────────────────────────────────────┘

32.2 Tablet Behavior

• يمكن إظهار قائمة الجلسات بجانب Workspace.
• يمكن فتح AI Assistant في لوحة جانبية.
• يمكن عرض Processes أو Logs في Panel إضافي.
• يجب السماح بتغيير عرض اللوحات.
• يجب ألا يؤدي فتح لوحة إلى تقليص مساحة Terminal إلى حد غير قابل للاستخدام.
• يمكن استخدام Multi-pane Layout عندما تكون المساحة كافية.

32.3 Tablet Interaction

• دعم اللمس.
• دعم لوحة مفاتيح خارجية.
• دعم اختصارات مناسبة عند وجود لوحة مفاتيح.
• عدم افتراض وجود Mouse.
• دعم التنقل بين اللوحات دون فقدان حالة الجلسة.

32.4 Tablet Visual Risks

• ازدحام اللوحات.
• تكرار المعلومات بين Header واللوحات.
• ظهور أكثر من Scroll Area دون وضوح.
• تضارب التفاعل باللمس مع اختيار النص.
• استخدام مساحة الشاشة لمجرد زيادة عدد الأدوات بدل تحسين التركيز.

───

33. Desktop — Professional Multi-Panel Terminal

الهدف هو توفير بيئة عمل احترافية متعددة اللوحات، مع المحافظة على هوية Mimir ووضوح الحالات.

33.1 Desktop Layout

┌────────────────────────────────────────────────────────────────────┐
│ Mimir Shell / Global Navigation │
├────────────────────────────────────────────────────────────────────┤
│ Terminal Header / Workspace / Session / Environment │
├──────────────┬───────────────────────────────────┬─────────────────┤
│ Navigation │ │ Context Panel │
│ │ │ │
│ Sessions │ │ AI Assistant │
│ Environments │ Terminal Workspace │ Processes │
│ History │ │ Logs │
│ Commands │ │ Details │
│ Connections │ │ │
│ │ │ │
├──────────────┴───────────────────────────────────┴─────────────────┤
│ Global Composer / Contextual Input │
└────────────────────────────────────────────────────────────────────┘

33.2 Desktop Capabilities

• Multi-session navigation.
• Resizable panels.
• Optional side panel.
• Clear session tabs or session switcher.
• Terminal output with sufficient space.
• Command Palette.
• Keyboard shortcuts.
• Contextual AI assistance.
• Logs and process inspection.
• Multi-window or split views only where supported by the application architecture.

33.3 Desktop Visual Priorities

1. Terminal output remains the dominant workspace.
2. Session identity and environment remain visible.
3. Panels do not compete equally for attention.
4. Important status indicators remain readable.
5. Advanced tools are discoverable without permanent visual clutter.
6. Dense information uses clear grouping and hierarchy.
7. Dark and Light themes preserve technical readability.

33.4 Desktop Risks

• Too many permanent sidebars.
• Excessive panel borders and shadows.
• Very small typography.
• Overloaded toolbars.
• Confusing multiple sessions with multiple processes.
• Making AI output visually indistinguishable from actual command output.

───

Part VIII — Responsive Comparison

34. Platform Comparison Matrix

Area| Android| Tablet| Desktop
Primary Layout| Single main view| Two or three panels| Multi-panel
Sessions| List / full page| Side panel| Persistent navigation panel
Workspace| Full screen| Main panel| Main central panel
AI Assistant| Sheet / separate view| Side panel| Persistent optional panel
Processes| Separate view| Contextual panel| Side panel / tab
Logs| Separate view| Expandable panel| Dedicated panel
File Explorer| Separate view| Side panel| Side panel
Command Palette| Overlay| Overlay| Overlay / shortcut
Navigation| Shell + contextual| Shell + panels| Shell + persistent navigation
Composer| Global Shell| Global Shell| Global Shell
Keyboard| Touch + software keyboard| Touch + optional keyboard| Keyboard-first
Advanced Tools| Contextual| Expandable| Discoverable persistent panels

35. Responsive Breakpoint Philosophy

لا يجب اعتماد أرقام Breakpoints عشوائية قبل اختبار كثافة المحتوى.

التغيير في التخطيط يعتمد على المساحة الفعلية المتاحة:

• Compact: شاشة ضيقة، واجهة أحادية المساحة.
• Medium: مساحة تسمح بإظهار قائمة ومحتوى رئيسي.
• Expanded: مساحة تسمح بلوحات متعددة.
• Large: مساحة واسعة تسمح بتوزيع الأدوات دون ضغط Workspace.

يجب أن تكون الأولوية لعرض Terminal الفعلي، وليس لنوع الجهاز وحده.

───

Part IX — Visual Design Quality Review

36. Visual Hierarchy Review

المراجعة التصميمية المطلوبة لكل صفحة:

المعيار| السؤال
Primary Focus| هل يعرف المستخدم فورًا أين يعمل؟
Secondary Actions| هل الأدوات المساندة واضحة دون منافسة المحتوى؟
Status Visibility| هل حالة الجلسة والتنفيذ مفهومة؟
Readability| هل الأوامر والمخرجات سهلة القراءة؟
Density| هل توجد معلومات كثيرة في مساحة صغيرة؟
Navigation| هل يمكن الوصول إلى الصفحات دون ضياع؟
Feedback| هل يعرف المستخدم نتيجة كل إجراء؟
Consistency| هل تتبع الصفحات نمطًا بصريًا واحدًا؟

37. Color and Theme Review

يجب الالتزام بـ Mimir Design Tokens:

• Pearl Light.
• Hybrid Dark / Light.
• Balanced Liquid Design.
• Adaptive Motion.

ضوابط Terminal:

• تباين مرتفع في منطقة المخرجات.
• ألوان حالة واضحة للنجاح والخطأ والتحذير والتشغيل.
• عدم استخدام اللون وحده لنقل المعنى.
• عدم وضع Glass قوي خلف النصوص التقنية.
• الحفاظ على وضوح الحدود بين اللوحات.
• عدم استخدام تأثيرات زخرفية تؤثر على الأداء أو القراءة.

38. Typography Review

يجب الفصل بين:

1. Global UI Font.
2. Terminal / Monospace Font.
3. AI Explanation Text.
4. Code / Command Content.

الإعدادات:

• حجم الخط العام لا يغير حجم Terminal Zoom تلقائيًا.
• Terminal Zoom مستقل عن Global Font Size.
• دعم تغيير الخط والحجم من إعدادات Mimir وفق ملكية الإعداد.
• الحفاظ على محاذاة الأحرف والمسافات في الأوامر.
• اختبار النصوص المختلطة العربية والإنجليزية والأرقام والرموز.
• الحفاظ على الاتجاه الصحيح للأوامر التقنية حتى داخل واجهة RTL.

39. Motion and Interaction Review

الحركة يجب أن تكون Adaptive:

• انتقالات قصيرة وواضحة.
• عدم تحريك منطقة المخرجات أثناء تنفيذ أمر دون سبب.
• عدم استخدام مؤثرات ثقيلة عند تحديث Logs.
• إظهار التغير في حالة التنفيذ دون إزعاج.
• احترام تقليل الحركة حيثما كان مدعومًا.

40. Accessibility Review

• أهداف لمس مناسبة.
• دعم تكبير النص.
• تباين كافٍ.
• حالات مرئية ومقروءة.
• تسميات مفهومة للأيقونات.
• دعم قارئات الشاشة.
• عدم الاعتماد على اللون وحده.
• دعم RTL/LTR.
• المحافظة على ترتيب التنقل المنطقي.
• إظهار Focus واضح عند استخدام لوحة المفاتيح.

───

Part X — Navigation and UX Failure Scenarios

41. Critical Scenarios

Scenario 1: User Leaves an Active Session

المشكلة: المستخدم يضغط Back ويظن أن الجلسة توقفت.

التصميم المطلوب: العودة لا توقف الجلسة، مع بقاء الحالة واضحة في Sessions.

Scenario 2: User Loses Connection

المشكلة: لا يعرف هل الأمر مستمر أم توقف.

التصميم المطلوب: حالة منفصلة مثل Connection Lost / Execution Status Unknown، مع عدم الادعاء بأن العملية نجحت.

Scenario 3: User Opens a Second Session

المشكلة: اختلاط المخرجات أو الأوامر بين الجلسات.

التصميم المطلوب: Session Identity واضحة، وسياق مستقل لكل جلسة.

Scenario 4: AI Suggests a Dangerous Command

المشكلة: المستخدم يظن أن الاقتراح آمن أو منفذ.

التصميم المطلوب: فصل الاقتراح عن التنفيذ، وإظهار التأثير والصلاحيات والموافقة عند الحاجة.

Scenario 5: User Navigates Away During Agent Run

المشكلة: يظن أن مغادرة الصفحة ألغت المهمة.

التصميم المطلوب: عرض حالة Run في موضع المتابعة المناسب، مع إجراءات مستقلة وصريحة.

Scenario 6: User Opens a Secret

المشكلة: تسرب قيمة سرية إلى History أو Logs أو AI Context.

التصميم المطلوب: استخدام Secure Vault وفق الصلاحيات، مع إخفاء الأسرار من السجلات والسياقات العامة.

Scenario 7: Unsupported Environment Feature

المشكلة: واجهة تعرض زرًا لوظيفة غير مدعومة.

التصميم المطلوب: إخفاء الوظيفة أو إظهار حالة غير مدعومة بوضوح، وعدم محاكاة نجاحها.

───

Part XI — Review Checklist

42. Android Review Checklist

[  ]  Terminal Home واضحة وغير مزدحمة.
[  ]  New Session وContinue Session بارزان.
[  ]  Terminal Output هو العنصر البصري الأساسي.
[  ]  لا توجد لوحات جانبية ضيقة.
[  ]  الأدوات الثانوية تظهر سياقيًا.
[  ]  Global Composer لا يتكرر.
[  ]  Bottom Navigation هو الخاص بـ Mimir.
[  ]  لوحة المفاتيح لا تغطي المحتوى المهم.
[  ]  Back لا يوقف الجلسة.
[  ]  المسودات محفوظة وفق السياسة.
[  ]  الحالات الحرجة واضحة.
[  ]  RTL/LTR يعملان بصريًا بشكل صحيح.
[  ]  النصوص التقنية مقروءة.
[  ]  لا توجد تأثيرات Glass تعيق القراءة.

43. Tablet Review Checklist

[  ]  يمكن عرض Sessions بجانب Workspace.
[  ]  يمكن فتح AI Assistant في Panel.
[  ]  لا تتقلص مساحة Terminal بشكل مفرط.
[  ]  يمكن استخدام اللمس ولوحة المفاتيح.
[  ]  اللوحات قابلة للتوسيع والإخفاء.
[  ]  لا يوجد ازدواج غير ضروري في Scroll Areas.
[  ]  لا تضيع حالة الجلسة عند التنقل.

44. Desktop Review Checklist

[  ]  Workspace مركزي وواضح.
[  ]  Navigation Panel منظم.
[  ]  Context Panel اختياري.
[  ]  الجلسات المتعددة لا تختلط.
[  ]  Command Palette متاحة.
[  ]  اختصارات لوحة المفاتيح واضحة.
[  ]  Logs وProcesses لا يطغيان على Terminal.
[  ]  التمييز بين AI Output وCommand Output واضح.
[  ]  كثافة المعلومات قابلة للتحكم.

45. Cross-Platform Review Checklist

[  ]  نفس نموذج التنقل الأساسي.
[  ]  نفس تعريفات الحالات.
[  ]  نفس الصلاحيات والسياسات.
[  ]  نفس قواعد Provenance.
[  ]  نفس قواعد الموافقة.
[  ]  نفس سلوك حفظ السياق.
[  ]  اختلاف التخطيط لا يغير معنى الوظيفة.
[  ]  لا توجد وعود بميزات غير مدعومة.
[  ]  لا يوجد تكرار لخدمات Shell العامة.

───

Part XII — Final Design Decisions and Pending Items

46. Decisions Preserved

هذه الوثيقة تحافظ على القرارات السابقة:

1. Terminal مساحة مستقلة عن Forge.
2. Shell يملك التنقل العام.
3. Shell يملك Global Composer.
4. Terminal لا يملك Approval Center مستقلًا.
5. الخدمات المشتركة مسؤولة عن الصلاحيات والموافقات والنشاط والبحث.
6. AI Assistant واجهة سياقية داخل Terminal.
7. Agent Run وSession وProcess وTask وTool Invocation حالات منفصلة.
8. Android هو نقطة البداية للتصميم responsive.
9. Tablet يستفيد من اللوحات الجانبية.
10. Desktop يقدم بيئة متعددة اللوحات.
11. لا يتم افتراض دعم SSH أو Docker أو Cloud أو Background Execution قبل التحقق.
12. لا يتم تنفيذ أمر لمجرد إدراجه أو اقتراحه.
13. لا يتم اعتبار نتيجة AI دليلًا مؤكدًا دون تحقق.
14. لا تتسرب الأسرار إلى History أو Logs أو AI Context افتراضيًا.

47. Pending Design Work

العناصر التالية تحتاج استكمالًا في مراحل التصميم التالية، ولا تعتبر قرارات تنفيذية نهائية:

• Visual mockups فعلية لصفحات Android الأساسية.
• مقارنة بصرية بين Compact وMedium وExpanded.
• تحديد قيم Breakpoints النهائية بعد اختبار المحتوى.
• اختبار شكل Workspace مع لوحة المفاتيح مفتوحة.
• مراجعة أحجام النصوص والأيقونات والمسافات.
• مراجعة حالات Session وProcess وAgent.
• مراجعة تجربة الأوامر الطويلة ومخرجات الأخطاء.
• مراجعة RTL/LTR بصريًا.
• مراجعة Tablet multi-panel.
• مراجعة Desktop multi-panel.

48. Recommended Visual Review Order

ترتيب المراجعة:

Stage 1 — Android

1. T01 Terminal Home.
2. T02 Sessions.
3. T03 Terminal Workspace.
4. T12 AI Terminal Assistant.
5. T10 Processes & Jobs.
6. T15 Logs & Diagnostics.
7. بقية الصفحات حسب خريطة التنقل.

Stage 2 — Tablet

1. Sessions + Workspace.
2. Workspace + AI Assistant.
3. Workspace + Processes / Logs.
4. Navigation and panel resizing.

Stage 3 — Desktop

1. Full Terminal Workspace.
2. Multi-session navigation.
3. Multi-panel layout.
4. Command Palette.
5. Advanced diagnostics and execution views.

49. Acceptance Criteria

تعتبر هذه المواصفة جاهزة للانتقال إلى مرحلة إعداد التصميمات المرئية عندما:

• تكون روابط الصفحات الـ17 واضحة.
• تكون الرحلات الأساسية محددة دون مسارات متعارضة.
• تكون قواعد Back وState Preservation واضحة.
• تكون نقاط الربط مع Forge وFiles وSecure Vault والخدمات المشتركة محددة.
• يكون Android layout قابلًا للفهم دون الاعتماد على Desktop.
• تكون فروق Tablet وDesktop محددة.
• تكون حالات الفشل والانقطاع والموافقة موثقة.
• لا توجد ازدواجية في مسؤوليات Shell أو Terminal.
• لا توجد وظيفة معروضة على أنها مدعومة دون أساس.

───

Final Status

• Navigation Map: Draft Completed.
• User Journey Map: Draft Completed.
• Cross-Space Navigation: Draft Completed.
• State Preservation & Back Behavior: Draft Completed.
• Android Responsive Review: Design Review Completed at Specification Level.
• Tablet Responsive Review: Design Review Completed at Specification Level.
• Desktop Responsive Review: Design Review Completed at Specification Level.
• Actual Visual Inspection: Pending Visual Mockups or Rendered Screens.
• Implementation Verification: Not Performed.
• Registry Approval: Pending.

الخطوة التالية بعد اعتماد هذه الوثيقة: إعداد التصورات المرئية الفعلية لشاشات Terminal على Android أولًا، مع البدء بـ Terminal Home وSessions وTerminal Workspace، ثم تطبيق نفس نظام التصميم على Tablet وDesktop.
