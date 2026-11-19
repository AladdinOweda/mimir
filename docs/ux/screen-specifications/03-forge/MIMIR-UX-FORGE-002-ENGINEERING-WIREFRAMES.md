Mimir Forge — Complete Engineering Wireframes & Interaction Specification

1. الغرض وحدود المسؤولية

Forge هي مساحة تطوير وإدارة مشاريع البرمجيات. تجمع ملفات المشروع ومحرر الكود والمهام والوكلاء والبناء والاختبارات والمراجعة والإصدارات.

Forge تملك:

• Project Workspace.
• Code Editing.
• Project Structure.
• Development Tasks.
• Build & Test Orchestration.
• Changes & Review.
• Artifacts & Releases.
• Development Environments Configuration.
• Sandbox Configuration and Oversight.

Forge لا تملك:

• تنفيذ أوامر النظام خارج عقد Terminal.
• إدارة هوية الوكلاء بدل Agent Runtime.
• إدارة النماذج بدل Model Runtime.
• فرض السياسات بدل Kernel.
• إدارة الأسرار بدل Vault.
• إثبات نجاح مستقل بدل Verification.

───

2. القواعد الهندسية

الخاصية| الهاتف
عرض مرجعي| 360dp
هامش المحتوى| 16dp
Header| 56dp
الحد الأدنى للمس| 48dp
المسافة بين الأقسام| 16dp
عرض بطاقات المشاريع| عرض المحتوى
منطقة المحرر| مرنة وقابلة للتوسيع

قواعد مهمة:

• الهاتف يعرض Workspace مبسطًا مع انتقالات بين الملفات والأدوات.
• التابلت يستخدم Split View عند توفر مساحة مناسبة.
• سطح المكتب يدعم محررًا متعدد الملفات ولوحات جانبية قابلة للترتيب.
• لا تضع جميع أدوات التطوير على الهاتف في واجهة مزدحمة.
• لا تعرض Build أو Test على أنه ناجح دون نتيجة فعلية.

───

3. المخططات الهندسية للشاشات الـ11

Screen 01 — Forge Home

الهدف: مركز المشاريع والنشاط والمهام.

┌────────────────────────────────┐
│ Forge Search │
├────────────────────────────────┤
│ Active Project │
│ ┌────────────────────────────┐ │
│ │ Project Name │ │
│ │ Status / Last Updated │ │
│ │ Open Workspace │ │
│ └────────────────────────────┘ │
│ │
│ Quick Actions │
│ [New Project] [Open Project] │
│ [New Task] [Build] │
│ │
│ Recent Projects │
│ Project Cards │
│ │
│ Active Tasks │
│ │
│ Recent Builds / Changes │
├────────────────────────────────┤
│ Home Projects Tasks More │
└────────────────────────────────┘

التفاعلات:

• New Project يفتح إنشاء مشروع.
• Open Project يعرض المشاريع المتاحة.
• Open Workspace يستعيد Project ID.
• Active Tasks يفتح Tasks & Planning.
• Build يطلب اختيار المشروع والهدف إذا لم يوجد سياق نشط.

الحالات: Empty Projects، Loading، Workspace Unavailable، Build Running، Project Error.

───

Screen 02 — Projects

الهدف: إدارة المشاريع ومصادرها وحالتها.

┌────────────────────────────────┐
│ ← Projects Search │
├────────────────────────────────┤
│ [All] [Recent] [Pinned] │
│ │
│ Project Filters │
│ │
│ ┌────────────────────────────┐ │
│ │ Project Name │ │
│ │ Location / Source │ │
│ │ Language / Framework │ │
│ │ Status / Updated │ │
│ │ Open ⋮ │ │
│ └────────────────────────────┘ │
│ │
│ ... │
├────────────────────────────────┤
│ + New Project │
└────────────────────────────────┘

المكونات:

• Project Card.
• Project Identity.
• Source Location.
• Technology Metadata.
• Last Updated.
• Project Health.
• Pin / Archive / Remove.

التفاعلات:

• إنشاء مشروع.
• فتح مشروع.
• استيراد مشروع.
• Pin/Unpin.
• Archive.
• Remove مع توضيح الفرق بين إزالة المرجع وحذف الملفات الفعلية.
• البحث والتصفية.

───

Screen 03 — Code Workspace

الهدف: تحرير ملفات المشروع وفهم بنيته.

الهاتف

┌────────────────────────────────┐
│ ← Project Name ⋮ │
├────────────────────────────────┤
│ File Path / Breadcrumb │
│ │
│ ┌────────────────────────────┐ │
│ │ Code Editor │ │
│ │ │ │
│ │ 1 source code │ │
│ │ 2 source code │ │
│ │ 3 source code │ │
│ │ │ │
│ └────────────────────────────┘ │
│ │
│ [Files] [Search] [Problems] │
│ │
│ Selected File / Diagnostics │
├────────────────────────────────┤
│ Save Diff AI Assist More │
└────────────────────────────────┘

التابلت وسطح المكتب

• File Explorer على الجانب.
• Editor في الوسط.
• Inspector/Problems على الجانب الآخر.
• Tabs للملفات المفتوحة.
• Bottom Panel للـ Terminal Output أو Build Logs عند الطلب.
• لا تفتح كل اللوحات افتراضيًا.

المكونات

• Project Explorer.
• File Tabs.
• Editor Surface.
• Syntax Highlighting.
• Line Numbers.
• Diagnostics Markers.
• Search & Replace.
• Diff View.
• AI Assistance Panel.
• Save State Indicator.

التفاعلات

• فتح/إنشاء/إعادة تسمية ملف.
• تعديل وحفظ.
• Undo/Redo.
• Search/Replace.
• فتح تعريف أو مرجع عند دعم Language Server.
• عرض Problems.
• مقارنة التغييرات.
• تطبيق Patch بعد مراجعة الفرق.
• استرجاع نسخة محفوظة.

ضوابط

• لا تطبق تغييرات AI بصمت على عدة ملفات.
• اعرض Diff قبل اعتماد التغييرات الواسعة.
• احتفظ بمصدر كل تعديل.
• لا تعرض الملف Saved قبل تأكيد الحفظ.
• التعامل مع تعارض الكتابة يجب أن يكون صريحًا.

───

Screen 04 — Toolchains & Environments

الهدف: إدارة الأدوات واللغات وأطر العمل وبيئات البناء.

┌────────────────────────────────┐
│ ← Toolchains & Environments │
├────────────────────────────────┤
│ Project Environment │
│ Platform / Runtime │
│ │
│ Detected Toolchains │
│ ┌────────────────────────────┐ │
│ │ Tool Name │ │
│ │ Version / Status │ │
│ │ Verify / Details │ │
│ └────────────────────────────┘ │
│ │
│ Dependencies │
│ Environment Variables │
│ Build Profiles │
│ │
│ Compatibility Warnings │
└────────────────────────────────┘

المكونات:

• Platform Selector.
• Runtime Detection.
• Toolchain List.
• Version Metadata.
• Dependency State.
• Environment Configuration.
• Compatibility Warnings.

التفاعلات:

• Discover Toolchains.
• Verify Version.
• Select Build Profile.
• Review Dependencies.
• Configure Environment.
• Import Configuration.
• لا يتم تثبيت أداة أو تغيير بيئة دون إذن مناسب.

القاعدة: اكتشاف أداة لا يعني أنها مثبتة أو جاهزة للتشغيل.

───

Screen 05 — Tasks & Planning

الهدف: تحويل أهداف التطوير إلى مهام وخطة قابلة للتتبع.

┌────────────────────────────────┐
│ ← Tasks & Planning Filter │
├────────────────────────────────┤
│ Project / Milestone │
│ │
│ [Board] [List] [Timeline] │
│ │
│ To Do │
│ ┌────────────────────────────┐ │
│ │ Task Title │ │
│ │ Priority / Owner │ │
│ │ Dependencies │ │
│ └────────────────────────────┘ │
│ │
│ In Progress │
│ ... │
│ │
│ Review / Done │
├────────────────────────────────┤
│ + New Task │
└────────────────────────────────┘

المكونات:

• Task Board.
• Task List.
• Timeline.
• Milestones.
• Dependencies.
• Owner / Agent.
• Priority.
• Acceptance Criteria.

التفاعلات:

• إنشاء وتعديل المهمة.
• تحديد الاعتماديات.
• إسناد المهمة إلى شخص أو Agent.
• نقل الحالة.
• ربط التغييرات بالمهمة.
• فتح تفاصيل التنفيذ.
• لا تصبح المهمة Done لمجرد اكتمال استدعاء أداة؛ يجب تحقق معيار القبول.

───

Screen 06 — Agents & Workers

الهدف: متابعة الوكلاء والعمال المشاركين في تطوير المشروع.

┌────────────────────────────────┐
│ ← Agents & Workers │
├────────────────────────────────┤
│ Active Agents │
│ │
│ ┌────────────────────────────┐ │
│ │ Agent Identity │ │
│ │ Assigned Task │ │
│ │ Runtime Status │ │
│ │ Model / Contribution │ │
│ │ Details │ │
│ └────────────────────────────┘ │
│ │
│ Queued / Waiting │
│ │
│ Completed / Failed │
├────────────────────────────────┤
│ Agent Activity │
└────────────────────────────────┘

المكونات:

• Agent Identity.
• Session State.
• Assigned Work.
• Model Attribution.
• Current Operation.
• Output / Artifact References.
• Failure / Handover State.

التفاعلات:

• عرض الوكيل.
• مراجعة المهمة.
• Pause/Resume عند دعم Runtime.
• Cancel.
• إعادة إسناد المهمة وفق قواعد التنسيق.
• استلام النتائج.
• فتح Diff أو Build Report الناتج.

ملكية المسؤوليات:

• Agent Runtime يملك الهوية والجلسة.
• Swarm يملك توزيع العمل.
• Workflow Runtime يملك تسلسل الخطوات.
• Forge يعرض العمل ويربطه بالمشروع.

───

Screen 07 — Build & Test Center

الهدف: تشغيل البناء والاختبارات ومراجعة النتائج.

┌────────────────────────────────┐
│ ← Build & Test Center │
├────────────────────────────────┤
│ Project / Build Profile │
│ │
│ Build Configuration │
│ Target / Variant │
│ │
│ [Build] [Run Tests] [Analyze] │
│ │
│ Active Execution │
│ Status / Phase / Runtime │
│ │
│ Test Results │
│ Passed / Failed / Skipped │
│ │
│ Build Artifacts │
│ │
│ Logs / Diagnostics │
└────────────────────────────────┘

المكونات:

• Build Profile Selector.
• Target Selector.
• Build Trigger.
• Test Trigger.
• Execution Monitor.
• Test Summary.
• Logs.
• Artifact List.

التفاعلات:

• Build.
• Run Tests.
• Cancel Execution.
• Open Logs.
• Inspect Failure.
• Compare Builds.
• Open Artifact.

ضوابط:

• التنفيذ يمر عبر KernelHost وTerminal/Execution Adapter حسب العقد.
• Forge لا تنفذ أوامر مباشرة خارج الحدود المعتمدة.
• Build Success يحتاج Exit Code ونتيجة موثوقة.
• Test Success يحتاج تقارير الاختبارات الفعلية.
• Build Failure لا يخفي Artifacts جزئية إن كانت موجودة.

───

Screen 08 — Changes & Code Review

الهدف: مراجعة التعديلات قبل دمجها أو اعتمادها.

┌────────────────────────────────┐
│ ← Changes & Code Review │
├────────────────────────────────┤
│ Project / Branch │
│ │
│ Change Summary │
│ Files Changed / Lines │
│ │
│ File List │
│ ┌────────────────────────────┐ │
│ │ File Name │ │
│ │ Added / Modified / Deleted │ │
│ └────────────────────────────┘ │
│ │
│ Diff Viewer │
│ │
│ Review Comments │
│ │
├────────────────────────────────┤
│ Request Review Approve │
└────────────────────────────────┘

المكونات:

• Change Summary.
• File Change List.
• Diff Viewer.
• Inline Comments.
• Review Status.
• Reviewer Identity.
• Approval History.

التفاعلات:

• فتح Diff.
• إضافة تعليق.
• طلب مراجعة.
• قبول أو رفض.
• طلب تعديلات.
• تطبيق Patch.
• مقارنة النسخ.

ضوابط:

• Approval لا يعني Merge تلقائيًا إلا إذا كان ذلك هو الإجراء المحدد صراحة.
• الاحتفاظ بمصدر كل تعديل.
• منع تطبيق Patch متعارض دون معالجة.
• عرض حالة الاختبارات المرتبطة بالتغيير.

───

Screen 09 — Artifacts & Releases

الهدف: إدارة الملفات الناتجة وإصدارات المشروع.

┌────────────────────────────────┐
│ ← Artifacts & Releases │
├────────────────────────────────┤
│ Releases │
│ ┌────────────────────────────┐ │
│ │ Version / Build ID │ │
│ │ Status / Date │ │
│ │ Verification │ │
│ │ Details │ │
│ └────────────────────────────┘ │
│ │
│ Artifacts │
│ File / Type / Size │
│ Source Build / Hash │
│ │
│ Release History │
└────────────────────────────────┘

المكونات:

• Release List.
• Artifact Registry View.
• Build Provenance.
• Version Metadata.
• Hash / Integrity.
• Verification Status.
• Distribution Status.

التفاعلات:

• فتح Artifact.
• تنزيل أو مشاركة وفق صلاحياته.
• إنشاء Release بعد تحقق شروطه.
• مقارنة الإصدارات.
• مراجعة مصدر الملف.
• إلغاء Release أو سحبه وفق صلاحيات النظام.

───

Screen 10 — Sandbox & Execution

الهدف: عرض حدود بيئة التنفيذ وعملياتها ومخاطرها.

┌────────────────────────────────┐
│ ← Sandbox & Execution │
├────────────────────────────────┤
│ Active Sandbox │
│ Environment / Isolation │
│ │
│ Resource Limits │
│ CPU / Memory / Storage │
│ │
│ Permissions │
│ Filesystem / Network │
│ │
│ Running Operations │
│ Operation / State │
│ │
│ Execution Logs │
│ │
│ Security Events │
└────────────────────────────────┘

المكونات:

• Sandbox Identity.
• Isolation State.
• Resource Limits.
• Network Policy.
• Filesystem Scope.
• Running Processes.
• Execution Logs.
• Cleanup Status.

التفاعلات:

• Inspect Sandbox.
• Request Permission.
• Stop Operation.
• Inspect Logs.
• Cleanup.
• Export Evidence.

ضوابط:

• لا تعرض Sandbox على أنه معزول إذا لم يثبت Runtime ذلك.
• لا توسع حدود الشبكة أو الملفات دون موافقة.
• لا تعرض أسرار البيئة.
• كل عملية مرتبطة بـ Operation ID وTask ID.

───

Screen 11 — Forge Settings

الهدف: إعداد سلوك Forge وبيئات التطوير والافتراضات.

┌────────────────────────────────┐
│ ← Forge Settings Search │
├────────────────────────────────┤
│ Workspace Defaults │
│ Editor Preferences │
│ Code Assistance │
│ Build & Test │
│ Toolchains │
│ Agent & Worker Defaults │
│ Sandbox & Security │
│ Artifact & Release │
│ Advanced │
│ Reset Defaults │
└────────────────────────────────┘

• الإعدادات اليومية أولًا.
• الإعدادات المتقدمة في صفحات فرعية.
• Live Preview للإعدادات المرئية.
• حفظ قيمة الإعداد ومصدرها.
• Reset يوضح نطاق التغيير.

───

4. انتقالات Forge

المصدر| الإجراء| الوجهة| الحالة الواجب الحفاظ عليها
Forge Home| Open Project| Projects / Workspace| Project ID
Projects| Open| Code Workspace| Project Context
Workspace| New Task| Tasks & Planning| Project ID
Task| Assign Agent| Agents & Workers| Task ID
Workspace| Build| Build & Test Center| Project Revision
Build Result| Inspect Changes| Changes & Code Review| Build ID
Build Result| Artifacts| Artifacts & Releases| Artifact IDs
Execution| Sandbox Details| Sandbox & Execution| Operation ID
Any Forge Screen| Settings| Forge Settings| Workspace State

───

5. تكامل Forge

Forge UI
│
▼
Forge Runtime
│
▼
Universal Kernel / KernelHost
├── Capability Resolution
├── Policy & Approval
├── Agent Runtime
├── Workflow Runtime
├── Swarm
├── Terminal / Execution Adapter
├── Model Runtime
├── Vault
├── Artifact Services
└── Verification

قواعد التكامل:

• Forge يملك Project State.
• Terminal يملك تنفيذ الأوامر.
• Agent Runtime يملك Agent Sessions.
• Workflow Runtime يملك Graph Execution.
• Model Runtime يملك تنفيذ النموذج.
• Vault يملك الأسرار.
• Verification يملك التحقق المستقل.
• لا توجد استدعاءات مباشرة بين المحركات خارج العقود.

───

6. اختبارات القبول

• Project Lifecycle.
• Project Import / Export.
• File Editing and Persistence.
• Multi-file Editing.
• Diff / Patch / Conflict.
• Toolchain Detection.
• Build Execution.
• Test Execution.
• Exit Code Accuracy.
• Sandbox Isolation.
• Resource Limits.
• Agent Assignment.
• Workflow Execution.
• Artifact Integrity.
• Release Provenance.
• Review Approval.
• Recovery.
• RTL/LTR.
• Responsive.
• Accessibility.
• Cross-engine Integration.

شرط الاعتماد: لا يُعلن اكتمال Forge قبل إثبات دورة مشروع كاملة: فتح المشروع، تعديل الملفات، مراجعة التغييرات، تشغيل البناء والاختبارات، إنتاج Artifact، والتحقق من مصدره وسلامته.
