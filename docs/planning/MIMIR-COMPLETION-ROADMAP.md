Mimir — Completion Roadmap

Browser, Chat, Forge & Terminal

الهدف: تحديد الأعمال المتبقية للوصول إلى محركات مترابطة وقابلة للاستخدام الفعلي، دون اعتبار وجود الشاشات أو الواجهات دليلًا على اكتمال الوظائف.

───

1. Browser Engine — قائمة الإكمال

A. التصميم وتجربة الاستخدام

العمل| المطلوب
الشاشات الأساسية| المخططات الهندسية الـ17
الشاشات الإضافية| Incognito، Extensions، Developer Tools، Diagnostics
Responsive Design| Android، Tablet، Desktop
Interaction Mapping| ربط كل إجراء بوجهته وحالته
Component Mapping| ربط المخططات بمكونات UI الفعلية
Accessibility| RTL/LTR، قارئ الشاشة، تكبير الخط، لوحة المفاتيح

B. Browser Runtime

• تثبيت Browser Runtime فعلي، لا الاكتفاء بواجهات أو Stubs.
• إنشاء وإدارة التبويبات والنوافذ.
• Navigation وHistory وReload وStop.
• إدارة حالات تحميل الصفحات وتعطلها واستعادتها.
• إدارة Profiles وSessions.
• Bookmarks وReading List.
• Downloads وإدارة الملفات الناتجة.
• Site Permissions وSite Data.
• Incognito بعزل حقيقي للبيانات والجلسات.
• Runtime Health وDiagnostics.

C. Page Understanding

• DOM Observation.
• Accessibility Tree.
• Interactive Elements Discovery.
• Form and Link Extraction.
• Selection and Page Metadata.
• Screenshot Capture.
• OCR عند الحاجة.
• Page State Fingerprinting.
• اكتشاف تغير الصفحة وإبطال المراجع القديمة.
• التعامل مع الصفحات الديناميكية والمحتوى المحمّل تدريجيًا.

D. Browser AI Controller

• فهم طلب المستخدم.
• تحديد نطاق الصفحات والتبويبات المسموح بها.
• إعداد خطة تنفيذ.
• اختيار النموذج المناسب.
• تحليل المخاطر.
• طلب الموافقة عند الحاجة.
• تنفيذ الإجراء.
• مراقبة التغير الناتج.
• التحقق من النتيجة.
• تقديم تقرير يوضح ما تم وما لم يتم.

القدرات المطلوبة:

• Read / Observe.
• Search / Extract.
• Click.
• Type.
• Select.
• Scroll.
• Form Fill.
• Submit ضمن حدود الموافقة.
• Multi-tab Operations.
• Multi-model Coordination.
• Pause / Resume / Cancel.
• Human Handover.
• Recovery after interruption.

E. Security

• حماية من Prompt Injection.
• اعتبار محتوى المواقع بيانات غير موثوقة.
• عزل بيانات المواقع عن تعليمات النظام والمستخدم.
• Permission Scopes دقيقة.
• Approval Tokens محدودة النطاق والوقت.
• Secret Isolation.
• حماية بيانات Incognito.
• منع الإجراءات الحساسة غير المصرح بها.
• Kill Switch.
• Audit Trail.

F. Browser Integration

• Universal Kernel / KernelHost.
• Capability Registry.
• Policy & Approval.
• Provider Registry وModel Runtime.
• Agent Runtime.
• Workflow Runtime.
• Swarm.
• Terminal.
• Forge.
• Vault.
• Verification.
• Shared Artifacts & Evidence.

G. اختبارات Browser

• Runtime Tests.
• Navigation Tests.
• Tabs & Sessions Tests.
• DOM / Accessibility Tests.
• Page Action Tests.
• Stale Observation Tests.
• Permission Tests.
• Prompt Injection Tests.
• Incognito Isolation Tests.
• Multi-model Tests.
• Cross-engine Integration Tests.
• Crash Recovery Tests.
• UI Navigation Tests.
• RTL/LTR and Responsive Tests.

───

2. Chat Engine — قائمة الإكمال

Chat ليس مجرد شاشة رسائل. يجب أن يكون مساحة محادثة موحدة مع النماذج والأدوات والذاكرة والمهام.

A. التصميم

الشاشات والواجهات المطلوب تحديدها هندسيًا:

1. Chat Home.
2. Conversation Workspace.
3. New Conversation / Model Selection.
4. Conversation Search.
5. Conversation History.
6. Conversation Details.
7. Attachments & Files.
8. Tool Activity & Execution.
9. Model Comparison.
10. Agent / Multi-model Conversation.
11. Memory & Context Inspector.
12. Message Details & Attribution.
13. Chat Settings.
14. Export / Share.
15. Error / Recovery States.

يجب أن تكون هذه الشاشات متصلة ببعضها وبنفس نظام التنقل العام، دون تكرار Composer في كل مساحة.

B. Conversation Runtime

• إنشاء المحادثات وإدارتها.
• حفظ الرسائل وترتيبها.
• تعديل الرسائل وحذفها.
• Regenerate وContinue.
• Streaming.
• إيقاف التوليد.
• Retry وفق قواعد تمنع التكرار غير المقصود.
• Branching للمحادثات عند دعمها.
• Conversation Search.
• Attachments.
• Export / Import وفق سياسات البيانات.
• مزامنة الحالة والتعامل مع الانقطاع.
• استعادة المحادثة بعد إعادة تشغيل التطبيق.

C. Model & Provider Integration

• Provider Discovery.
• Provider Enablement.
• Model Registry.
• Model Capability Metadata.
• Model Selection.
• Context Window Management.
• Tool Calling.
• Streaming Support.
• Structured Output.
• Error Mapping.
• Rate Limits.
• Timeout and Cancellation.
• Local / Cloud Execution.
• Model Attribution.

يجب أن يظل اكتشاف النموذج منفصلًا عن تفعيله، وأن يعرض Chat النموذج المستخدم فعليًا لا النموذج الذي كان المستخدم يتوقع استخدامه.

D. Memory & Context

• Short-term Conversation Context.
• Long-term Memory Integration.
• Episodic Memory.
• Semantic Memory.
• Procedural Memory.
• RAG للمعرفة الخارجية.
• Context Assembly.
• Context Budgeting.
• Source Attribution.
• User-Controlled Memory Access.
• Context Inspector.
• Incognito / Temporary Conversation Isolation.

لا ينبغي إرسال كامل ذاكرة المستخدم أو جميع المحادثات إلى النموذج افتراضيًا. يجب تحديد السياق المطلوب والغرض منه.

E. Tools & Agents

• Tool Registry.
• Capability Resolution.
• Tool Permissions.
• Tool Execution.
• Tool Results.
• Agent Handover.
• Workflow Invocation.
• Swarm Delegation.
• Browser Integration.
• Forge Integration.
• Terminal Integration.
• Verification Results.

كل استدعاء أداة يجب أن يحتفظ بمعرف العملية وحالتها ونتيجتها وأدلتها.

F. Chat Security

• Secret Redaction.
• Attachment Safety.
• Prompt Injection Handling.
• Tool Approval.
• Permission Scoping.
• Data Isolation.
• Audit Trail.
• Safe Error Handling.

G. Chat Tests

• Conversation Persistence.
• Streaming.
• Message Ordering.
• Model Switching.
• Context Assembly.
• Memory Isolation.
• Tool Invocation.
• Tool Failure.
• Cancellation.
• Retry and Idempotency.
• Multi-model Attribution.
• Attachment Handling.
• Cross-engine Handover.
• Recovery and Synchronization.

───

3. Forge Engine — قائمة الإكمال

Forge هو مساحة تطوير وبناء وإدارة مشاريع البرمجيات، وليس مجرد واجهة لعرض الأكواد أو تشغيل أوامر.

A. التصميم

يجب أن تغطي المخططات الهندسية صفحات Forge المعتمدة سابقًا:

1. Forge Home.
2. Projects.
3. Code Workspace.
4. Toolchains & Environments.
5. Tasks & Planning.
6. Agents & Workers.
7. Build & Test Center.
8. Changes & Code Review.
9. Artifacts & Releases.
10. Sandbox & Execution.
11. Forge Settings.

B. Project Runtime

• إنشاء المشاريع وفتحها.
• Project Metadata.
• Project Structure.
• Source Files.
• File Operations.
• File Versioning.
• Project Context.
• Workspace State.
• Git Integration عند توفره.
• Branches / Commits / Diff.
• Project Import / Export.
• Project Recovery.

C. Code Workspace

• محرر كود فعلي.
• Syntax Highlighting.
• Code Navigation.
• Search / Replace.
• Multi-file Editing.
• Diff Viewer.
• Diagnostics.
• Code Suggestions.
• AI-assisted Editing.
• Patch Application.
• Conflict Detection.
• Undo / Redo.
• File Save State.

D. Build & Test

• Toolchain Discovery.
• Environment Validation.
• Build Execution.
• Test Execution.
• Linting.
• Static Analysis.
• Dependency Management.
• Build Logs.
• Test Reports.
• Failure Diagnosis.
• Artifact Generation.
• Reproducible Build Metadata.

لا يجوز أن يظهر Build أو Test ناجحًا دون نتيجة حقيقية من Executor.

E. Agents & Planning

• Task Decomposition.
• Work Assignment.
• Agent Identity.
• Worker Sessions.
• Execution Tracking.
• Dependencies.
• Parallel Work.
• Conflict Detection.
• Code Review Handover.
• Approval Gates.
• Recovery.

F. Sandbox & Security

• Isolated Execution.
• Filesystem Boundaries.
• Network Policies.
• Resource Limits.
• Command Approval.
• Dependency Risk Checks.
• Secrets Isolation.
• Execution Logs.
• Cleanup.
• Artifact Provenance.

G. Forge Integration

• KernelHost.
• Capability Registry.
• Agent Runtime.
• Workflow Runtime.
• Swarm.
• Model Runtime.
• Terminal.
• Browser.
• Vault.
• File Engine.
• Verification.
• Artifact Registry.

Forge يملك إدارة مشروع التطوير وعمليات البناء، بينما Terminal يملك تنفيذ الأوامر، ولا ينبغي إنشاء مسارين متنافسين لتنفيذ الأمر نفسه.

H. Forge Tests

• Project Lifecycle.
• File Operations.
• Workspace Persistence.
• Code Editing.
• Diff and Patch.
• Toolchain Detection.
• Build Execution.
• Test Execution.
• Sandbox Isolation.
• Agent Coordination.
• Artifact Integrity.
• Release Verification.
• Recovery and Synchronization.

───

4. Terminal Engine — قائمة الإكمال

Terminal هو محرك تنفيذ أوامر وبيئات طرفية حقيقية، وليس مجرد نافذة نصية تشبه الطرفية.

A. التصميم

الشاشات المطلوبة:

1. Terminal Home.
2. Terminal Workspace.
3. Session Manager.
4. Shell / Environment Selector.
5. Command History.
6. Running Processes.
7. File & Directory Browser.
8. Environment Variables.
9. SSH / Remote Connections عند دعمها.
10. Command Approval & Security.
11. Terminal Settings.
12. Diagnostics.

B. Execution Runtime

• Shell Runtime حقيقي.
• Session Creation.
• Session Persistence.
• Command Execution.
• Input / Output Streaming.
• Exit Codes.
• Process Management.
• Signals.
• Working Directory.
• Environment Variables.
• Interactive Commands.
• PTY Support عند الحاجة.
• Timeouts.
• Cancellation.
• Process Recovery.

C. Platform Support

• Android / Termux Adapter عند اعتماده.
• Local Runtime.
• Remote Runtime عند دعم الاتصال.
• Container / Sandbox Adapter عند توفره.
• Platform Capability Discovery.
• Environment Detection.

يجب أن توضح الواجهة الفرق بين تنفيذ محلي وتنفيذ بعيد أو معزول.

D. Terminal AI

• Explain Command.
• Suggest Command.
• Generate Script.
• Explain Error.
• Diagnose Output.
• Propose Fix.
• Execute with Approval.
• Verify Execution Result.

اقتراح النموذج لأمر لا يعني السماح بتنفيذه. التنفيذ يخضع لسياسات Kernel والموافقات وحدود البيئة.

E. Security

• Command Risk Classification.
• Destructive Command Protection.
• Working Directory Restrictions.
• Environment Secret Protection.
• Network Restrictions.
• Sandbox Policy.
• Approval Tokens.
• Audit Logs.
• Process Isolation.
• Output Redaction.

F. Terminal Integration

• KernelHost.
• Capability Registry.
• Policy / Approval.
• Forge.
• Browser.
• Chat / Model Runtime.
• Agent Runtime.
• Workflow Runtime.
• Swarm.
• Vault.
• Verification.
• File Engine.

G. Terminal Tests

• Command Execution.
• Exit Code Accuracy.
• Streaming.
• Cancellation.
• Timeout.
• Process Lifecycle.
• Session Recovery.
• Working Directory.
• Environment Isolation.
• Permission Enforcement.
• Secret Redaction.
• Sandbox.
• Cross-engine Execution.
• Android Platform Tests.

───

5. العقود المشتركة المطلوبة بين المحركات

يجب مراجعة العقود الموجودة أولًا، ثم إعادة استخدامها أو تطويرها. لا تنشئ ملفًا جديدًا إذا كان العقد موجودًا باسم أو موقع مختلف.

العقد / الخدمة| المسؤولية
Universal Request / Result| صيغة الطلب والنتيجة المشتركة
Engine Manifest| تعريف المحرك وقدراته
Capability Contract| وصف كل قدرة وشروط استخدامها
KernelHost Contract| نقطة الربط بين Kernel والمحركات
Policy Contract| تقييم السياسات
Approval Contract| الموافقات المقيدة
Provider Contract| تعريف مزودي النماذج
Model Runtime Contract| تنفيذ طلبات النماذج
Agent Runtime Contract| الهوية والجلسات والسياق والأدوات
Workflow Contract| تعريف وتشغيل تدفقات العمل
Swarm Contract| توزيع العمل والتنسيق
Artifact Contract| الملفات والمخرجات
Evidence Contract| الأدلة ومصادرها
Verification Contract| التحقق المستقل
Audit Event Contract| تسجيل الأحداث
Context Package Contract| مشاركة السياق المصرح به
Handover Contract| تسليم العمل بين المحركات
Lifecycle Contract| بدء المحرك وإيقافه وصحته

قاعدة التكامل

User Interface
│
▼
Application Shell
│
▼
Universal Kernel / KernelHost
│
├── Policy & Approval
├── Capability Resolution
├── Provider / Model Services
├── Agent Runtime
├── Workflow Runtime
├── Swarm Coordination
├── Evidence & Verification
│
▼
Independent Engines
├── Browser
├── Chat
├── Forge
└── Terminal

المحركات لا تستدعي بعضها مباشرة. طلب التكامل يمر عبر KernelHost والعقود والصلاحيات المحددة.

───

6. السيناريوهات التكاملية التي يجب إثباتها

السيناريو| مسار العمل| دليل النجاح
بحث عبر Browser وتحليل في Chat| Browser → Context Package → Chat| المصادر محفوظة مع نسبتها
البحث عن خطأ برمجي وإصلاحه| Browser → Forge| التغيير موثق ومراجع
تشغيل أمر من Forge| Forge → KernelHost → Terminal| Exit Code ومخرجات حقيقية
تنفيذ مهمة متعددة المحركات| Chat → Workflow → Browser/Forge/Terminal| حالة كل خطوة محفوظة
تفويض مهمة لوكلاء متعددين| Agent Runtime / Swarm| توزيع ومخرجات قابلة للتتبع
استخدام سر لتسجيل الدخول| Browser → Vault Reference| السر لا يصل إلى النموذج
تعطل محرك أثناء مهمة| Runtime → Recovery| استعادة الحالة دون ادعاء نجاح
إلغاء عملية حساسة| UI → Kernel → Engine| تأكيد الإلغاء من التنفيذ
التحقق من نتيجة بناء| Forge → Verification| دليل مستقل على النتيجة
العودة بين المساحات| Shell Navigation| استعادة الحالة دون تكرار العمليات

───

7. معيار اكتمال كل محرك

يجب ألا تعتمد حالة المحرك على الانطباع البصري. استخدم مستويات موحدة:

الحالة| المعنى
PLANNED| مخطط فقط
SPECIFIED| المواصفات مكتوبة
CONTRACT_DEFINED| العقود محددة
STUB| تنفيذ وهمي أو غير مكتمل
ADAPTER_IMPLEMENTED| يوجد Adapter منفذ
INTEGRATION_TESTED| اجتاز اختبارات التكامل
VERIFIED| توجد أدلة تحقق
PRODUCTION_READY| اجتاز بوابات الإصدار
BLOCKED| متوقف بسبب اعتماد أو مشكلة
UNAVAILABLE| غير متاح على المنصة الحالية

لا تنتقل أي قدرة من STUB إلى VERIFIED دون تنفيذ فعلي واختبار ودليل.

───

8. ترتيب العمل المقترح

المرحلة| العمل| الناتج
P0| جرد المستودع والوثائق والعقود| خريطة الواقع الحالي
P1| مطابقة ADRs وBlueprint| مرجع حاكم موحد
P2| مراجعة العقود المشتركة| Contract Matrix
P3| إغلاق مخططات Browser| UI Engineering Reference
P4| تنفيذ Browser Runtime| تصفح فعلي
P5| Browser AI & Security| تفاعل آمن قابل للتحقق
P6| إغلاق تصميم Chat وتنفيذه| Chat Runtime مكتمل
P7| إغلاق تصميم Forge وتنفيذه| Development Runtime مكتمل
P8| إغلاق تصميم Terminal وتنفيذه| Execution Runtime مكتمل
P9| التكامل بين المحركات| Cross-engine Workflows
P10| الاختبارات والأمان والتعافي| Release Evidence
P11| مراجعة الإصدار| Production Readiness

───

9. التسليم النهائي المطلوب

لكل محرك يجب أن تتوفر:

1. UX Specification.
2. Engineering Wireframes.
3. Interaction Specification.
4. Component Mapping.
5. Responsive Specification.
6. Engine Architecture.
7. Contract Mapping.
8. Runtime Implementation.
9. Security Model.
10. Integration Tests.
11. Verification Evidence.
12. Known Limitations.
13. Release Checklist.
14. Implementation Status.

قرار الحوكمة النهائي: لا يُعلن اكتمال Browser أو Chat أو Forge أو Terminal لأن الواجهات تبدو مكتملة. الاكتمال يتطلب أن تكون الوظائف حقيقية، والعقود سليمة، والتكاملات قابلة للإثبات، والأخطاء معلنة بصدق.
