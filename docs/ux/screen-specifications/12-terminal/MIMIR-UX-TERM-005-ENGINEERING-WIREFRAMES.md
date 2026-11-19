Mimir Terminal — Complete Engineering Wireframes & Interaction Specification

1. الغرض وحدود المسؤولية

Terminal هو محرك تنفيذ الأوامر وإدارة الجلسات والعمليات والبيئات الطرفية.

Terminal يملك:

• Shell Sessions.
• Command Execution.
• Input/Output Streaming.
• Process Lifecycle.
• Working Directory.
• Environment Configuration.
• Command History.
• Terminal Runtime Health.
• Local/Remote Execution Adapters عند توفرها.

Terminal لا يملك:

• سياسات Kernel.
• إدارة أسرار Vault.
• إدارة مشروع Forge.
• هوية Agent Runtime.
• تنسيق Swarm.
• تنفيذ Workflow Graph.
• اختيار النموذج وتنفيذه.

───

2. القواعد الهندسية

الخاصية| الهاتف
عرض مرجعي| 360dp
هامش المحتوى| 16dp
ارتفاع Header| 56dp
الحد الأدنى للمس| 48dp
منطقة Terminal| مرنة وقابلة للتمرير
Composer/Command Input| ثابت أسفل الجلسة

• الهاتف: جلسة واحدة مركزة، مع الوصول إلى الجلسات الأخرى عبر Session Manager.
• التابلت: Terminal مع لوحة جلسات أو ملفات جانبية.
• Desktop: جلسات متعددة ولوحات قابلة للتقسيم.
• النص الطرفي يستخدم خطًا أحادي المسافة.
• لا يُختزل الإخراج الطويل أو يُحذف دون إمكانية الوصول إليه.
• يجب أن يكون الفرق بين Local وRemote وSandbox واضحًا دائمًا.

───

3. المخططات الهندسية للشاشات

Screen 01 — Terminal Home

┌────────────────────────────────┐
│ Terminal Search │
├────────────────────────────────┤
│ Active Environment │
│ Platform / Shell / Status │
│ │
│ Quick Actions │
│ [New Session] [Open Session] │
│ [Run Command] [Environments] │
│ │
│ Recent Sessions │
│ ┌────────────────────────────┐ │
│ │ Session Name │ │
│ │ Shell / Working Directory │ │
│ │ Status / Last Activity │ │
│ └────────────────────────────┘ │
│ │
│ Running Processes │
│ │
│ Recent Commands │
├────────────────────────────────┤
│ Home Projects Tasks More │
└────────────────────────────────┘

التفاعلات:

• New Session ينشئ جلسة فعلية.
• Open Session يستعيد جلسة موجودة.
• Run Command يفتح جلسة أو يطلب اختيار جلسة.
• Running Processes يفتح شاشة العمليات.
• Recent Commands يعرض سجل الأوامر.

───

Screen 02 — Terminal Workspace

الهدف: مساحة التنفيذ الرئيسية.

┌────────────────────────────────┐
│ ← Session Name Status ⋮ │
├────────────────────────────────┤
│ Environment / Shell │
│ Working Directory │
├────────────────────────────────┤
│ │
│ Terminal Output │
│ │
│  Command Input Send │
│ [Keyboard] [History] [More] │
└────────────────────────────────┘

المكونات

• Session Header.
• Environment Identity.
• Shell Identity.
• Working Directory.
• Terminal Output Surface.
• Command Input.
• Process Status.
• Keyboard Controls.
• Output Search.

التفاعلات

• إدخال أمر.
• إرسال الأمر إلى Runtime.
• استقبال Streaming Output.
• إرسال Input تفاعلي عند دعم PTY.
• إيقاف العملية.
• تغيير حجم الخط.
• نسخ النص.
• البحث داخل الإخراج.
• الانتقال إلى سجل الأوامر.

ضوابط

• لا يُرسل الأمر إذا لم تكن الجلسة جاهزة.
• لا يُظهر نجاحًا قبل استلام Exit Code.
• Exit Code غير متاح لا يُعامل كأنه صفر.
• لا تُعاد الأوامر الحساسة تلقائيًا بعد انقطاع الاتصال.
• يجب فصل Input عن Output بصريًا.

───

Screen 03 — Session Manager

┌────────────────────────────────┐
│ ← Sessions + New │
├────────────────────────────────┤
│ [All] [Running] [Paused] │
│ [Disconnected] [Closed] │
│ │
│ ┌────────────────────────────┐ │
│ │ Session Name │ │
│ │ Shell / Environment │ │
│ │ Working Directory │ │
│ │ Status / Runtime │ │
│ │ Open / ⋮ │ │
│ └────────────────────────────┘ │
│ │
│ ... │
└────────────────────────────────┘

المكونات:

• Session Identity.
• Shell.
• Execution Location.
• Runtime State.
• Last Activity.
• Attached Processes.
• Reconnect Status.

التفاعلات:

• فتح جلسة.
• إنشاء جلسة.
• إغلاق جلسة.
• إعادة الاتصال إذا كانت البيئة تدعم ذلك.
• Rename.
• عرض العمليات المرتبطة.

قاعدة: جلسة UI ليست دليلًا على بقاء عملية النظام حيّة. يجب فحص Runtime عند استعادة الجلسة.

───

Screen 04 — Shell & Environment Selector

┌────────────────────────────────┐
│ ← Shell & Environment │
├────────────────────────────────┤
│ Execution Location │
│ Local / Remote / Sandbox │
│ │
│ Available Shells │
│ Shell Name / Version │
│ │
│ Environment │
│ OS / Architecture │
│ Runtime / Path │
│ │
│ Compatibility │
│ Capabilities │
│ Limitations │
├────────────────────────────────┤
│ Select Environment │
└────────────────────────────────┘

التفاعلات:

• اكتشاف البيئات.
• اختيار Shell.
• فحص الإصدار.
• فحص القدرات.
• عرض القيود.
• اختيار البيئة للجلسة الجديدة.

قواعد:

• لا تعرض Shell على أنه متاح قبل التحقق.
• لا تعرض Remote Runtime دون اتصال فعلي.
• لا تعرض Sandbox على أنه معزول دون إثبات.
• لا تغير بيئة جلسة نشطة بصمت.

───

Screen 05 — Command History

┌────────────────────────────────┐
│ ← Command History Search │
├────────────────────────────────┤
│ Filters │
│ Session / Date / Status │
│ │
│ Today │
│ ┌────────────────────────────┐ │
│ │ Command │ │
│ │ Session / Time │ │
│ │ Exit Code / Status │ │
│ │ Reuse / Details │ │
│ └────────────────────────────┘ │
│ │
│ Earlier │
│ ... │
└────────────────────────────────┘

التفاعلات:

• البحث.
• نسخ الأمر.
• عرض تفاصيل التنفيذ.
• إعادة استخدام الأمر بعد مراجعة.
• تصفية حسب الجلسة والوقت والحالة.

ضوابط:

• لا تحفظ أسرارًا في سجل الأوامر دون حماية.
• إخفاء القيم الحساسة عند العرض والتصدير.
• Reuse لا يعني إعادة تنفيذ تلقائية.

───

Screen 06 — Running Processes

┌────────────────────────────────┐
│ ← Running Processes Refresh │
├────────────────────────────────┤
│ Environment / Session │
│ │
│ ┌────────────────────────────┐ │
│ │ Process Name │ │
│ │ PID / Parent │ │
│ │ Started / Runtime │ │
│ │ Resource Usage │ │
│ │ Stop / Details │ │
│ └────────────────────────────┘ │
│ │
│ ... │
└────────────────────────────────┘

المكونات:

• Process Identity.
• PID.
• Parent Process.
• Session Reference.
• Runtime.
• Resource Usage.
• Process State.

التفاعلات:

• Refresh.
• Inspect.
• Send Signal.
• Terminate عند السماح.
• Open Owning Session.

ضوابط:

• لا يُعرض PID من بيانات مخزنة قديمة كأنه عملية حية.
• لا تُرسل إشارة إلى عملية دون التحقق من البيئة والهوية.
• العمليات الحساسة تتطلب موافقة مناسبة.

───

Screen 07 — File & Directory Browser

┌────────────────────────────────┐
│ ← Files & Directories │
├────────────────────────────────┤
│ Environment / Current Path │
│ │
│ [..] │
│ 📁 Directory A │
│ 📁 Directory B │
│ 📄 File A │
│ 📄 File B │
│ │
│ Selected Item │
│ Type / Size / Permissions │
├────────────────────────────────┤
│ Open / Copy Path / More │
└────────────────────────────────┘

التفاعلات:

• تنقل في المجلدات.
• فتح ملف عبر File Engine أو Forge حسب نوع العملية.
• نسخ المسار.
• عرض Metadata.
• طلب عملية ملفية.

قواعد:

• Terminal لا ينشئ File Manager منافسًا لـ Intelligence Files.
• File Engine يملك خدمات الملفات المشتركة.
• Terminal يعرض ملفات البيئة التي يسمح Runtime بالوصول إليها.
• المسارات خارج النطاق تُرفض بوضوح.

───

Screen 08 — Environment Variables

┌────────────────────────────────┐
│ ← Environment Variables │
├────────────────────────────────┤
│ Search │
│ │
│ Variable Name │
│ Value (Masked if Sensitive) │
│ Source / Scope │
│ │
│ Variable Name │
│ Value │
│ │
│ Add Variable │
└────────────────────────────────┘

المكونات:

• Variable Name.
• Masked Value.
• Source.
• Scope.
• Read-only / Editable.
• Secret Reference.

التفاعلات:

• عرض المتغيرات المسموح بها.
• تعديل متغير ضمن صلاحيات البيئة.
• إضافة متغير.
• إزالة متغير.
• ربط Secret Reference من Vault.

ضوابط:

• لا تُعرض قيمة السر الخام افتراضيًا.
• لا تُرسل Environment Variables إلى النموذج تلقائيًا.
• لا تُخزن الأسرار ضمن History.
• Vault يملك السر، وTerminal يتلقى مرجعًا محدودًا أو حقنًا آمنًا وقت التنفيذ.

───

Screen 09 — Remote Connections

الهدف: إدارة الاتصالات البعيدة إذا كانت مدعومة ومفعّلة.

┌────────────────────────────────┐
│ ← Remote Connections + │
├────────────────────────────────┤
│ Saved Connections │
│ ┌────────────────────────────┐ │
│ │ Connection Name │ │
│ │ Host / Identity │ │
│ │ Last Checked / Status │ │
│ │ Connect / Details │ │
│ └────────────────────────────┘ │
│ │
│ Connection Diagnostics │
└────────────────────────────────┘

المكونات:

• Connection Profile.
• Host Identity.
• Authentication Reference.
• Trust State.
• Connection State.
• Last Check.
• Capability Summary.

التفاعلات:

• إنشاء Connection Profile.
• اختبار الاتصال.
• الاتصال.
• قطع الاتصال.
• مراجعة بصمة الخادم أو هويته عند دعم البروتوكول.
• إلغاء اتصال.

ضوابط:

• لا تُخزن كلمات المرور أو المفاتيح الخاصة في الواجهة أو النموذج.
• Vault يحتفظ بالأسرار.
• لا تتجاوز الموافقة على الاتصال سياسات الشبكة.
• لا يتم الاتصال تلقائيًا لمجرد فتح ملف تعريف محفوظ.

───

Screen 10 — Command Approval & Security

┌────────────────────────────────┐
│ ← Command Security │
├────────────────────────────────┤
│ Pending Approval │
│ │
│ Command Summary │
│ Working Directory │
│ Environment │
│ │
│ Risk Assessment │
│ Filesystem / Network / Process │
│ │
│ Requested Permissions │
│ │
│ [Approve Once] [Deny] │
│ │
│ Security History │
└────────────────────────────────┘

التفاعلات:

• مراجعة الأمر.
• عرض نطاق التنفيذ.
• الموافقة لمرة واحدة أو رفضه.
• إلغاء موافقة غير مستخدمة.
• مراجعة سجل القرارات.

قواعد:

• الموافقة مرتبطة بالأمر والبيئة والجلسة والنطاق والوقت.
• تعديل الأمر بعد الموافقة يبطل الموافقة السابقة.
• الأوامر التدميرية لا تُنفذ بسبب موافقة عامة قديمة.
• لا يمكن للنموذج اعتماد أمره بنفسه.

───

Screen 11 — Terminal Settings

┌────────────────────────────────┐
│ ← Terminal Settings Search │
├────────────────────────────────┤
│ Appearance │
│ Font / Size / Cursor │
│ │
│ Terminal Behavior │
│ Scrollback / Input │
│ │
│ Default Shell │
│ Session Defaults │
│ │
│ Command History │
│ │
│ AI Assistance │
│ │
│ Security & Approvals │
│ │
│ Reset Defaults │
└────────────────────────────────┘

• خط أحادي المسافة.
• حجم النص.
• سلوك لوحة المفاتيح.
• حد Scrollback.
• Shell الافتراضي.
• سياسة History.
• إعدادات AI Assistance.
• إعدادات الأمان.

───

Screen 12 — Terminal Diagnostics

┌────────────────────────────────┐
│ ← Terminal Diagnostics Refresh│
├────────────────────────────────┤
│ Overall Health │
│ │
│ Runtime │
│ Shell / Adapter / Platform │
│ │
│ Capabilities │
│ Execution / PTY / Signals │
│ │
│ Active Sessions │
│ Active Processes │
│ │
│ Recent Errors │
│ │
│ Recovery / Export Report │
└────────────────────────────────┘

التفاعلات:

• فحص Runtime.
• فحص Shell.
• فحص PTY.
• فحص Capabilities.
• مراجعة الأخطاء.
• تصدير تقرير منقح.
• إعادة الاتصال أو إعادة التشغيل عند دعمها.

قاعدة: لا تُخفى حالات Stub أو Unavailable أو Not Ready خلف واجهة Terminal قابلة للكتابة.

───

4. دورة تنفيذ الأمر

User Input
│
▼
Terminal Request
│
▼
Kernel Capability Resolution
│
▼
Policy / Risk Assessment
│
├── Denied → Return Error
│
├── Approval Required → Wait
│
▼
Execution Adapter
│
▼
Process / PTY Runtime
│
▼
Streaming Output
│
▼
Exit Code / Final State
│
▼
Evidence / Audit

النتيجة يجب أن تتضمن:

• Request ID.
• Operation ID.
• Session ID.
• Environment ID.
• Command Reference.
• Started / Completed timestamps.
• Output Reference.
• Exit Code.
• Final Status.
• Error Details إن وجدت.

───

5. تكامل Terminal

Terminal UI
│
▼
Terminal Runtime
│
▼
Universal Kernel / KernelHost
├── Capability Registry
├── Policy & Approval
├── Execution Adapter
├── Vault
├── Agent Runtime
├── Workflow Runtime
├── Model Runtime
├── Forge
├── Browser
├── File Services
└── Verification

• Forge يطلب تنفيذ أوامر تطوير عبر عقد Terminal.
• Agent Runtime يطلب تنفيذ قدرة مصرح بها.
• Workflow Runtime ينظم تسلسل الخطوات.
• Model Runtime يشرح أو يقترح، لكنه لا ينفذ مباشرة.
• Vault يزود التنفيذ بمراجع أسرار آمنة.
• Verification يفحص النتائج وفق متطلبات العملية.

───

6. اختبارات القبول

• Shell Discovery.
• Session Creation.
• Command Execution.
• Streaming Output.
• Interactive Input.
• Exit Code Accuracy.
• Timeout.
• Cancellation.
• Process Lifecycle.
• Session Recovery.
• Environment Isolation.
• Working Directory Restrictions.
• Variable Redaction.
• Vault Secret Injection.
• Command Risk Evaluation.
• Approval Token Scope.
• Local / Remote Differentiation.
• PTY Support.
• Crash Recovery.
• Cross-engine Integration.
• Android Compatibility.
• RTL/LTR and Accessibility.

شرط الاعتماد: لا يُعتبر Terminal مكتملًا لمجرد عرض Prompt وإخراج نصي. يجب إثبات جلسة حقيقية، وتنفيذ حقيقي، ومخرجات وExit Code صحيحين، وإدارة عمليات وصلاحيات وتعافٍ موثوق.
