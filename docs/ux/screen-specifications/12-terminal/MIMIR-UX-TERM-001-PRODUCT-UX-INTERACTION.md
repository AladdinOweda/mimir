───

document_id: PENDING_DOCUMENT_REGISTRY
title: Mimir Terminal — Comprehensive Product, UX & Interaction Specification
document_type: Engine Product and UX Specification
status: Comprehensive Design Draft — Pending Owner Review and Registry Approval
language: Arabic
scope: Product Vision, Information Architecture, Screens, Interactions, Execution, AI Assistance, Agents, Environments, Security, Responsive UX, Accessibility
related_documents:

• MIMIR-ARCH-BP-001
• MIMIR-GOV-CON-001
• MIMIR-UX-SHELL-001
• MIMIR-UX-INT-001
• MIMIR-UX-SCREEN-001
• MIMIR-FORGE-001

───

Mimir Terminal

المواصفة الشاملة للتصميم والوظائف وتجربة الاستخدام

1. الرؤية والهدف

Mimir Terminal هو مساحة احترافية متكاملة للتفاعل المباشر مع أنظمة التشغيل وبيئات التنفيذ وسطر الأوامر، مع دمج الذكاء الاصطناعي والوكلاء والأتمتة في تجربة واحدة متماسكة.

لا ينبغي أن يكون Terminal مجرد نافذة سوداء لكتابة الأوامر، ولا مجرد واجهة Chat تستطيع اقتراح أوامر. يجب أن يجمع بين:

1. تجربة Terminal حقيقية وسريعة.
2. إدارة جلسات Shell متعددة.
3. إدارة بيئات التنفيذ المحلية والبعيدة والسحابية.
4. تشغيل الأوامر والعمليات ومراقبتها.
5. مساعدة AI تفهم سياق الجلسة والبيئة والملفات.
6. وكلاء AI قادرين على تنفيذ مهام ضمن صلاحيات محددة.
7. مكتبة أوامر وSnippets قابلة لإعادة الاستخدام.
8. إدارة الملفات والمسارات المرتبطة ببيئة التنفيذ.
9. مراقبة العمليات والموارد والمخرجات.
10. سجل كامل قابل للبحث والمراجعة.
11. نظام أمان وصلاحيات وموافقات واضح.
12. تكامل مع Forge وبقية Mimir دون تكرار مسؤولياتها.

الهدف أن يستطيع المستخدم المبتدئ تنفيذ مهمة معقدة باللغة الطبيعية، وأن يستطيع المستخدم المحترف العمل مباشرة بالأوامر والاختصارات والتحكم الدقيق.

1.1 المرجع التنافسي

يستفيد Mimir Terminal من أنماط الاستخدام الاحترافية التي جعلت منتجات مثل Cursor وClaude Code وHermes وغيرها مفيدة، دون تقليد تصميمها أو نسخ خصائصها حرفيًا.

المجال| ما نريد تحقيقه في Mimir
Terminal الاحترافي| سرعة الإدخال، جلسات متعددة، اختصارات، تاريخ الأوامر، وضوح المخرجات
AI Coding Assistants| فهم السياق، التخطيط، اقتراح الأوامر، شرح النتائج، المساعدة في التشخيص
Agentic Execution| تنفيذ مهام متعددة الخطوات، متابعة الحالة، تسليم العمل، والتوقف الآمن
أدوات الإدارة| البيئات، الاتصالات، العمليات، السجلات، الصلاحيات
تجربة Mimir| واجهة موحدة، Provenance، تكامل مع Forge، وإدارة مركزية للمهام والموافقات

هذه أهداف تصميمية وليست ادعاءً بأن جميع القدرات متاحة بالفعل أو أن جميع المنتجات المرجعية تقدم الوظائف نفسها.

2. موقع Terminal داخل Mimir

2.1 استقلال Terminal عن Forge

يجب الحفاظ على الحدود التالية:

Terminal مسؤول عن:

• واجهة سطر الأوامر.
• جلسات Shell.
• تنفيذ الأوامر.
• إدارة العمليات المرتبطة بجلساته.
• بيئات التنفيذ.
• الاتصال ببيئات بعيدة عند دعمها.
• تاريخ الأوامر والمخرجات.
• التفاعل المباشر مع النظام.
• تشغيل أدوات التنفيذ من خلال عقود واضحة.

Forge مسؤول عن:

• إدارة دورة حياة مشاريع البرمجيات.
• استكشاف المشروع وفهم بنيته.
• تحرير الكود.
• التخطيط الهندسي.
• مراجعة التغييرات.
• Build وTest.
• الإصدارات والـArtifacts.

يمكن لـ Forge طلب تنفيذ أمر عبر عقد تنفيذ عام، لكن Terminal لا يتحول إلى جزء داخلي من Forge، ولا يعيد تنفيذ وظائف محرر الكود أو إدارة المشروع.

2.2 علاقة Terminal بالخدمات العامة

Terminal يستهلك الخدمات المشتركة ولا ينشئ بدائل خاصة به لها:

• Global Identity & Authentication.
• Permissions & Authorization.
• Model Provenance.
• Projects & Tasks.
• Activity & Audit Trail.
• Notifications.
• Unified Search.
• Files & Knowledge.
• Secrets Vault.
• Connectivity & Synchronization.
• Approval Center.
• Agent Runtime.
• Orchestration.
• Workflow Runtime.

لا يملك Terminal نسخة مستقلة من هذه الخدمات.

2.3 قاعدة Kernel

Kernel لا يعرف Terminal كحالة خاصة.

يسجل Terminal قدراته عبر عقود عامة، ويعلن:

• Capability Manifest.
• العمليات المدعومة.
• أنواع البيئات.
• متطلبات التشغيل.
• الصلاحيات المطلوبة.
• إصدارات العقود.
• حدود الموارد.
• آليات الصحة والتعافي.

إضافة Terminal أو تعطيله أو استبداله لا يجب أن تتطلب تعديل منطق Kernel.

3. هوية التجربة والتصميم البصري

3.1 الشخصية البصرية

يستخدم Terminal نظام Mimir Liquid Design المعتمد:

• Balanced visual personality.
• Adaptive motion.
• Pearl Light.
• Hybrid Dark/Light.
• مظهر احترافي هادئ.
• معلومات كثيفة لكن منظمة.
• تمييز واضح بين النصوص والأوامر والمخرجات والتنبيهات.

لا ينبغي أن يتحول Terminal إلى واجهة زجاجية مزخرفة تقلل وضوح النص.

3.2 منطقة الطرفية

يجب أن تكون مساحة Terminal نفسها عالية الوضوح، بخلفية مستقرة وتباين قوي.

خصائصها:

• Monospace font.
• تلوين Syntax عند دعم النوع المعروض.
• ألوان مميزة للأوامر والمخرجات والأخطاء والتحذيرات.
• تمييز واضح لموجه الأوامر Prompt.
• تحديد النص والنسخ واللصق.
• دعم النصوص الطويلة.
• دعم Unicode وRTL/LTR.
• عدم كسر محاذاة النصوص البرمجية.
• إمكانية إخفاء الزخارف واللوحات المحيطة عند التركيز.

3.3 التحكم في القراءة

إعدادات Terminal المستقلة:

• زيادة وتصغير حجم خط الطرفية.
• Reset للخط والحجم.
• ضبط Line Height.
• ضبط Cursor Style.
• ضبط Cursor Blink.
• ضبط الألوان والتباين.
• اختيار Font متاح.
• إعدادات Word Wrap.
• إعدادات Scrollback.
• إعدادات Selection وCopy.

تكبير Terminal لا يغير حجم خط Mimir العام ولا حجم محرر Forge.

3.4 أوضاع العرض

يحتوي Terminal على ثلاثة أوضاع رئيسية:

1. Focused Terminal: مساحة طرفية واسعة دون لوحات جانبية غير ضرورية.
2. Split Workspace: طرفيتان أو أكثر مع توزيع قابل للتعديل عند دعم البيئة.
3. Terminal + AI: الطرفية مع مساعد AI وسياق الجلسة والنتائج.

تتوفر هذه الأوضاع بحسب إمكانات المنصة وحجم الشاشة.

4. خريطة التنقل والمعلومات

4.1 التبويبات الرئيسية داخل Terminal

التبويبات السياقية:

1. Overview.
2. Sessions.
3. Environments.
4. Processes.
5. Commands.
6. Connections.
7. History.
8. Security.
9. Settings.

لا يلزم عرض جميع التبويبات في شريط ظاهر على الهاتف؛ يمكن وضع بعضها داخل قائمة More أو قائمة سياقية.

4.2 الصفحات الرئيسية

تتكون مواصفة Terminal من الصفحات التالية:

الرمز| الصفحة| الوظيفة
T01| Terminal Home| مركز التحكم الرئيسي
T02| Sessions| إدارة الجلسات
T03| Terminal Workspace| مساحة العمل الطرفية الأساسية
T04| Session Details| تفاصيل الجلسة
T05| Command History| تاريخ الأوامر
T06| Commands & Snippets| مكتبة الأوامر
T07| Environments| إدارة بيئات التنفيذ
T08| Environment Details| تفاصيل البيئة
T09| File & Path Explorer| استكشاف الملفات والمسارات
T10| Processes & Jobs| إدارة العمليات والمهام
T11| Connections & Hosts| الاتصالات والأنظمة البعيدة
T12| AI Terminal Assistant| المساعدة الذكية السياقية
T13| Agents & Execution Runs| متابعة تنفيذ الوكلاء
T14| Permissions & Security| الصلاحيات والأمان
T15| Logs & Diagnostics| السجلات والتشخيص
T16| Terminal Settings| الإعدادات
T17| Command Palette| الوصول السريع للأوامر والوظائف

Command Palette وAI Assistant قد يظهران كطبقات أو Panels، لكنهما جزء من بنية الصفحات والتجربة وليس بالضرورة وجهتين دائمتين في التنقل.

5. T01 — Terminal Home

الهدف

تكون الصفحة مركز قيادة مختصرًا يعرض حالة Terminal ويوجه المستخدم إلى أهم العمليات.

التخطيط

A. Header

• عنوان Terminal.
• مؤشر بيئة التنفيذ النشطة.
• مؤشر Local / Cloud / Remote / Hybrid.
• حالة الاتصال.
• زر إنشاء جلسة جديدة.
• قائمة الإجراءات الإضافية.

لا يُعرض نوع تنفيذ غير متحقق منه على أنه حقيقة.

B. Quick Actions

بطاقات أو اختصارات واضحة:

• New Session.
• Open Last Session.
• Select Environment.
• Connect to Host.
• Run Command.
• Ask AI.
• Browse History.

تُخفى أو تُعطّل الإجراءات غير المدعومة مع تفسير السبب.

C. Active Sessions

تعرض الجلسات النشطة:

• اسم الجلسة.
• البيئة.
• Shell.
• وقت البدء.
• حالة التنفيذ.
• آخر نشاط.
• وجود عملية تعمل حاليًا.
• مؤشر AI Agent إذا كان يعمل داخل الجلسة.

D. Environment Overview

ملخص البيئات:

• Local Environments.
• Cloud Environments.
• Remote Hosts.
• Containers أو Sandboxes إذا كانت مدعومة.
• البيئات المتوقفة أو غير المتاحة.

E. Recent Commands

آخر الأوامر التي نفذها المستخدم، مع:

• الأمر.
• البيئة.
• الجلسة.
• الوقت.
• النتيجة.
• Exit Code عندما يكون متاحًا فعليًا.

F. Running Processes

ملخص العمليات:

• عدد العمليات النشطة.
• عمليات طويلة التشغيل.
• عمليات تنتظر الإدخال.
• عمليات متوقفة أو فاشلة.
• عمليات تحتاج موافقة.

G. AI Activity

يعرض أعمال AI المرتبطة بـ Terminal:

• مهام قيد التخطيط.
• مهام قيد التنفيذ.
• مهام تنتظر الموافقة.
• مهام اكتملت مع نتائج.
• مهام فشلت مع سبب واضح.

H. Recent Activity

آخر الأحداث:

• فتح جلسة.
• اتصال ببيئة.
• تنفيذ أمر.
• إيقاف عملية.
• طلب موافقة.
• انتهاء مهمة AI.

حالات الصفحة

• First Use.
• No Environment.
• No Active Session.
• Loading.
• Offline.
• Partial Availability.
• Normal.
• Service Error.

في أول استخدام، لا تظهر لوحة فارغة؛ تقدم الصفحة شرحًا مبسطًا لإنشاء أول بيئة وجلسة.

6. T02 — Sessions

الهدف

إدارة جميع جلسات Terminal في مكان واحد.

طرق العرض

• List View.
• Compact Cards.
• Split/Grouped View على الشاشات الكبيرة.

بيانات الجلسة

• Session Name.
• Session ID.
• Environment.
• Shell Type.
• Working Directory.
• Owner.
• Created At.
• Last Activity.
• Session State.
• Active Process.
• Attached Agent إن وجد.
• Execution Location.
• Resource Usage إذا كان متاحًا.

حالات الجلسة

يجب التمييز بين:

• Created.
• Starting.
• Ready.
• Running.
• Waiting for Input.
• Paused.
• Reconnecting.
• Disconnected.
• Completed.
• Failed.
• Terminated.
• Unknown.

لا تخلط حالة الجلسة بحالة العملية أو الوكيل.

إجراءات الجلسة

• Open.
• Rename.
• Duplicate Configuration.
• Pin.
• Archive.
• Close.
• Inspect.
• Attach AI Context.
• Open in Split View.

إغلاق الجلسة لا يعني بالضرورة إيقاف جميع العمليات المرتبطة بها؛ يجب توضيح الأثر قبل الإغلاق.

إنشاء جلسة جديدة

خطوات الإنشاء:

1. اختيار البيئة.
2. اختيار Shell متاح.
3. اختيار Working Directory.
4. تحديد إعدادات الجلسة.
5. تحديد مستوى الصلاحيات عند الحاجة.
6. مراجعة الإعدادات.
7. إنشاء الجلسة.

يمكن توفير Quick Start باستخدام إعدادات افتراضية آمنة.

7. T03 — Terminal Workspace

هذه أهم صفحة في المنتج.

7.1 التكوين العام

تتكون مساحة العمل من:

A. Workspace Header

• اسم الجلسة.
• اسم البيئة.
• موقع التنفيذ.
• الاتصال.
• حالة الجلسة.
• Working Directory.
• زر الجلسات.
• زر تقسيم الشاشة.
• قائمة الأدوات.

B. Terminal Tabs

تسمح بالتنقل بين الجلسات المفتوحة.

كل Tab يعرض:

• اسم الجلسة.
• حالة التشغيل.
• مؤشر وجود عملية نشطة.
• علامة عند وجود خطأ حديث.
• قائمة إجراءات.

C. Terminal Canvas

منطقة الطرفية الرئيسية.

تشمل:

• Prompt.
• Command Input.
• Command Output.
• Error Output.
• Selection.
• Scrollback.
• Search in Output.
• Copy.
• Clear View دون حذف سجل التنفيذ.

يجب التمييز بين مسح العرض ومسح التاريخ الحقيقي.

D. Command Input

إدخال الأوامر يدعم:

• الكتابة المباشرة.
• اللصق.
• الاقتراحات.
• إكمال الأوامر عند توفر دعم حقيقي.
• التنقل في History.
• تنفيذ الأمر.
• إلغاء الإدخال قبل التنفيذ.
• اختصارات لوحة المفاتيح.

يجب ألا يرسل التطبيق أمرًا لمجرد أن المستخدم اختار اقتراحًا.

E. Context Bar

يعرض:

• Environment.
• Working Directory.
• Shell.
• Execution Mode.
• Permission Profile.
• Connected Host.

يمكن توسيعه لرؤية تفاصيل أكثر.

F. Contextual Side Panel

لوحة قابلة للفتح والإغلاق تضم:

• AI Assistant.
• Session Info.
• Files.
• Processes.
• Command Details.
• Agent Activity.

لا يجب أن تظهر كل هذه اللوحات في الوقت نفسه على الهاتف.

7.2 سلوك تنفيذ الأمر

عند تنفيذ أمر:

1. التحقق من أن الجلسة جاهزة.
2. التحقق من البيئة والصلاحيات.
3. تحديد ما إذا كان الأمر يحتاج موافقة.
4. إرسال الأمر إلى جهة التنفيذ الصحيحة.
5. عرض حالة الإرسال والبدء.
6. عرض المخرجات أثناء وصولها.
7. عرض النتيجة النهائية وExit Code إذا توفر.
8. تسجيل الأمر والنتيجة والبيئة والوقت.
9. حفظ علاقة التنفيذ بالجلسة والمستخدم أو الوكيل.

يجب دعم:

• Success.
• Failed.
• Partial Result.
• Cancelled.
• Timed Out.
• Unknown Outcome.
• Awaiting Input.

لا يجوز اختلاق Exit Code أو إظهار نجاح عند غياب دليل.

7.3 الأوامر طويلة التشغيل

عند تشغيل أمر يستمر فترة طويلة:

• يظهر مؤشر واضح للعملية.
• يمكن الانتقال إلى صفحات أخرى دون فقدان المتابعة.
• يمكن العودة إلى الجلسة.
• يمكن إظهار المخرجات الجديدة.
• يمكن طلب إيقاف العملية إذا كانت آلية الإيقاف مدعومة.
• لا يتسبب إخفاء الشاشة في إيقاف العملية تلقائيًا.

7.4 تقسيم الطرفية

يدعم التصميم:

• Horizontal Split.
• Vertical Split.
• Multiple Panes.
• Resize Panels.
• Move Session Between Panes.
• Close Pane.

العدد الأقصى الفعلي يعتمد على قدرة البيئة والجهاز، ولا يجب فرض عدد ثابت غير قابل للتحقق.

7.5 أوضاع العرض

• Terminal Only.
• Terminal + Assistant.
• Terminal + Files.
• Terminal + Processes.
• Multi-Terminal.

7.6 قائمة السياق

عند تحديد نص أو أمر أو نتيجة:

• Copy.
• Search.
• Explain with AI.
• Save as Snippet.
• Run Again.
• Open Related File عند دعم ذلك.
• Inspect Output.

أي إجراء يعيد تنفيذ أمر يجب أن يعرض الأمر كاملًا والبيئة المستهدفة، ويطلب تأكيدًا إذا كان ذا أثر مهم.

8. T04 — Session Details

صفحة تفاصيل مستقلة عن مساحة الطرفية.

أقسامها

Overview

• Session Identity.
• Status.
• Environment.
• Shell.
• Working Directory.
• Created / Updated.

Configuration

• Variables المسموح بعرضها.
• Startup Configuration.
• Terminal Dimensions.
• Encoding.
• Scrollback.

لا تعرض قيم المتغيرات التي قد تحتوي أسرارًا دون تفويض واضح.

Activity

• أوامر الجلسة.
• العمليات.
• الأحداث.
• طلبات الموافقة.
• أخطاء الاتصال.

Execution Context

• Local/Cloud/Remote.
• Host.
• Runtime.
• Agent Attachment.
• Provenance.

Controls

• Rename.
• Reconnect.
• Close.
• Archive.
• Export Session Log.
• Terminate Associated Process إذا كان مدعومًا وبعد توضيح أثره.

9. T05 — Command History

الهدف

إنشاء تاريخ موحد قابل للبحث والمراجعة.

الفلاتر

• Session.
• Environment.
• Date.
• Command.
• Exit Status.
• User / Agent.
• Execution Type.
• Working Directory.

بطاقة الأمر

• نص الأمر.
• وقت التنفيذ.
• الجلسة.
• البيئة.
• المدة.
• Exit Code إن توفر.
• حالة النتيجة.
• مصدر التنفيذ: User / AI / Agent / Workflow.

تفاصيل الأمر

تحتوي على:

• الأمر الكامل.
• المخرجات.
• الأخطاء.
• المدة.
• البيئة.
• Working Directory.
• Execution ID.
• Provenance.
• الموافقات المرتبطة.
• الملفات المتأثرة إذا أمكن التحقق منها.

الإجراءات

• Copy.
• Run Again.
• Explain.
• Save as Snippet.
• Compare Results.
• Export.
• Delete History Entry وفق سياسة الاحتفاظ.

قبل إعادة تنفيذ أمر قديم، يجب إعادة تقييم الصلاحيات والسياق؛ لا يعني نجاحه سابقًا أنه آمن الآن.

10. T06 — Commands & Snippets

الهدف

مكتبة شخصية ومنظمة للأوامر المتكررة.

التصنيفات

• Favorites.
• Recent.
• System.
• Development.
• Git.
• Files & Directories.
• Networking.
• Package Management.
• Diagnostics.
• Custom.

هذه تصنيفات تنظيمية وليست ضمانًا لدعم كل الأدوات.

بيانات Snippet

• Name.
• Description.
• Command Template.
• Variables.
• Category.
• Tags.
• Compatible Shells.
• Required Environment.
• Risk Classification.
• Author.
• Created / Updated.
• Usage Count.

المتغيرات

يمكن تعريف قيم إدخال مثل:

• Project Path.
• File Name.
• Branch Name.
• Port.
• Target Environment.

لا تُحقن القيم داخل الأوامر بطريقة غير آمنة. يجب استخدام آلية Parameter Binding مناسبة للبيئة أو إظهار الأمر النهائي قبل التنفيذ.

الإجراءات

• Create.
• Edit.
• Duplicate.
• Favorite.
• Categorize.
• Search.
• Preview.
• Run.
• Export / Import.

AI Snippet Builder

يسمح للمستخدم بوصف الأمر المطلوب باللغة الطبيعية، ثم يقوم AI بـ:

1. فهم الهدف.
2. اقتراح الأمر.
3. شرح أجزاء الأمر.
4. توضيح المتطلبات.
5. تحديد المخاطر.
6. عرض الأمر النهائي.
7. انتظار الموافقة عند الحاجة.

لا تُضاف أوامر AI تلقائيًا إلى المكتبة دون موافقة المستخدم.

11. T07 — Environments

الهدف

إدارة الأماكن التي يمكن تنفيذ الأوامر داخلها.

أنواع البيئات

• Local Environment.
• Remote Environment.
• Cloud Environment.
• Container Environment عند الدعم.
• Isolated Sandbox عند توفر عزل حقيقي.
• Project-Scoped Environment.

لا يُعرض نوع بيئة كخيار متاح إلا إذا كان مزود التنفيذ يدعمه فعليًا.

Environment Overview

• الاسم.
• النوع.
• الحالة.
• نظام التشغيل أو المنصة.
• Shells المتاحة.
• الموارد المتاحة.
• وقت آخر تحقق.
• الاتصال.
• حالة الصحة.

إنشاء بيئة

المستخدم يختار:

1. نوع البيئة.
2. مزود التنفيذ.
3. الاسم.
4. إعدادات التشغيل.
5. حدود الموارد.
6. مجلد العمل.
7. الصلاحيات.
8. سياسة الاتصال.
9. مراجعة الملخص.
10. إنشاء البيئة.

اكتشاف الأدوات

يمكن للبيئة تقديم معلومات عن:

• Shell.
• Runtime.
• Package Manager.
• أدوات النظام.
• المسارات.
• الإصدارات.
• الأدوات غير المتاحة.

يجب تمييز المعلومات المكتشفة فعليًا عن التوصيات والافتراضات.

Environment Assistant

يساعد المستخدم على:

• فهم البيئة.
• اكتشاف الأدوات الناقصة.
• مقارنة الإصدارات.
• شرح مشكلات التوافق.
• اقتراح إعدادات مناسبة.
• إعداد خطة تجهيز.

التثبيت أو التعديل يتطلب صلاحية وموافقة وفق مستوى الخطورة.

12. T08 — Environment Details

الأقسام

Overview

حالة البيئة ومعلوماتها الأساسية.

Capabilities

ما تدعمه البيئة فعليًا:

• Interactive Shell.
• Process Control.
• File Access.
• Network Access.
• Persistent Storage.
• Background Execution.
• Session Restore.

Tools

الأدوات المثبتة وإصداراتها عند إمكانية التحقق.

Resources

• CPU.
• Memory.
• Disk.
• Network.

لا تظهر أرقام غير متاحة أو تقديرية وكأنها قياسات فعلية.

Access & Security

• صلاحيات القراءة.
• صلاحيات الكتابة.
• صلاحيات الشبكة.
• حدود الموارد.
• عزل البيئة.
• مدة الصلاحية.

Sessions

الجلسات المرتبطة بهذه البيئة.

Maintenance

• Health Check.
• Reconnect.
• Refresh Capabilities.
• Disable.
• Remove.

قبل إزالة بيئة، يجب توضيح ما سيحدث للجلسات والبيانات والعمليات المرتبطة بها.

13. T09 — File & Path Explorer

هذه الصفحة مخصصة لاستعراض الملفات والمسارات داخل بيئة التنفيذ، وليست بديلًا عن Mimir Files.

الوظائف

• استعراض المجلدات.
• فتح المسارات.
• البحث عن ملف.
• عرض معلومات الملف.
• نسخ المسار.
• فتح الملف في Forge عند توافق التكامل.
• طلب تفسير الملف من AI.
• إظهار الملفات المرتبطة بالجلسة أو المشروع.

بيانات الملف

• الاسم.
• المسار.
• النوع.
• الحجم إذا أمكن التحقق.
• آخر تعديل.
• صلاحية الوصول.
• البيئة المالكة للمسار.

الإجراءات

• Open.
• Copy Path.
• Reveal Parent.
• Search.
• Refresh.
• Open in Forge.
• Explain with AI.

عمليات الحذف والنقل والكتابة والتعديل ليست أزرارًا افتراضية سريعة؛ تحتاج توضيحًا للمسار والأثر والصلاحيات والتأكيد المناسب.

حدود الملكية

Terminal يتعامل مع الملفات من خلال صلاحيات البيئة وعقود الملفات المعتمدة.

لا ينشئ Terminal مستودع ملفات موازيًا، ولا يفترض أن جميع المسارات المحلية أو البعيدة متاحة.

14. T10 — Processes & Jobs

الهدف

مراقبة العمليات التي بدأت من خلال Terminal أو ترتبط بجلساته.

القائمة

• Process Name.
• PID إذا توفر.
• Session.
• Environment.
• Started At.
• Duration.
• State.
• Resource Usage إذا توفر.
• Owner.
• Parent Process إذا توفر.

حالات العملية

• Starting.
• Running.
• Waiting.
• Paused.
• Stopping.
• Completed.
• Failed.
• Terminated.
• Unknown.

تفاصيل العملية

• الأمر الأصلي.
• البيئة.
• الجلسة.
• المخرجات المرتبطة.
• مدة التشغيل.
• الموارد.
• سجل الحالة.
• علاقات العمليات التابعة عند توفرها.

التحكم

• Inspect.
• Open Session.
• View Output.
• Request Stop.
• Terminate عند توفر الصلاحية.
• Explain with AI.

لا يظهر زر Pause إلا إذا كان الإيقاف المؤقت مدعومًا فعليًا.

قبل الإنهاء:

• توضيح العملية.
• توضيح أثر الإنهاء.
• بيان ما إذا كانت هناك عمليات تابعة.
• طلب تأكيد مناسب.

لا يجوز أن يؤدي إيقاف واجهة Terminal أو التنقل منها إلى قتل عملية دون اختيار المستخدم.

15. T11 — Connections & Hosts

الهدف

إدارة الاتصالات ببيئات التنفيذ والأنظمة البعيدة.

أنواع الاتصالات

بحسب الدعم الفعلي:

• Local.
• SSH.
• Remote Execution Provider.
• Cloud Workspace.
• Container Provider.
• Managed Runtime.

Connections Overview

• اسم الاتصال.
• المضيف.
• نوع الاتصال.
• الحالة.
• آخر اتصال.
• البيئة المرتبطة.
• الصلاحيات.
• هوية المستخدم.

إنشاء اتصال

1. اختيار نوع الاتصال.
2. إدخال بيانات المضيف المطلوبة.
3. اختيار وسيلة المصادقة.
4. اختيار طريقة تخزين بيانات الاعتماد الآمنة.
5. تحديد نطاق الصلاحيات.
6. اختبار الاتصال.
7. مراجعة النتائج.
8. حفظ الاتصال.

إدارة بيانات الاعتماد

• لا تخزن كلمات المرور أو مفاتيح SSH داخل ملفات إعدادات عادية.
• استخدم Secrets Vault أو مزودًا آمنًا مناسبًا.
• لا تعرض السر كاملًا بعد حفظه.
• لا تسجل السر في Logs أو History.
• لا تنقل السر إلى AI Context افتراضيًا.
• يمكن إلغاء الاتصال أو سحب التفويض.

حالات الاتصال

• Not Configured.
• Connecting.
• Connected.
• Authentication Failed.
• Unreachable.
• Reconnecting.
• Expired.
• Revoked.
• Unknown.

لا تعاد المحاولات التي قد تسبب آثارًا جانبية دون سياسة مناسبة.

16. T12 — AI Terminal Assistant

الهدف

جعل Terminal قابلًا للاستخدام للمبتدئ والمحترف معًا.

المساعد ليس مجرد Chat منفصل؛ يجب أن يفهم السياق المسموح به للجلسة والبيئة والأوامر والمخرجات.

الوصول

يمكن الوصول إليه عبر:

• Global Mimir Composer مع سياق Terminal.
• Contextual AI Panel.
• Explain من قائمة السياق.
• Command Palette.
• اختصار لوحة المفاتيح.

لا تنشأ نافذة Composer ثانية مستقلة عن Composer العام.

السياق الذي يمكن للمساعد استخدامه

بحسب الصلاحيات:

• الجلسة الحالية.
• نوع Shell.
• البيئة.
• Working Directory.
• آخر الأوامر.
• المخرجات.
• الأخطاء.
• الملفات المحددة.
• العمليات المرتبطة.
• المشروع المرتبط.
• معلومات Knowledge المصرح بها.

يجب أن يرى المستخدم ملخص السياق المستخدم، وأن يستطيع إزالة أو إضافة عناصر السياق.

لا تدخل Secrets أو بيانات الاعتماد في السياق تلقائيًا.

قدرات المساعد

Explain

شرح الأمر أو المخرجات بلغة بسيطة.

Suggest

اقتراح أمر مناسب لهدف المستخدم.

Diagnose

تحليل الخطأ والبحث عن السبب المحتمل.

Plan

تحويل الهدف إلى خطوات تنفيذ واضحة.

Prepare

تجهيز الأوامر دون تنفيذها.

Execute

تنفيذ الأوامر بعد فحص الصلاحيات والموافقة اللازمة.

Repair

اقتراح إصلاح مشكلة أو تنفيذ إصلاح مصرح به.

Compare

مقارنة نتائج التنفيذ أو الخيارات.

Learn

شرح المفاهيم والأوامر للمستخدم أثناء العمل.

مستويات تدخل AI

1. Explain Only: يشرح دون تنفيذ.
2. Suggest Commands: يقترح أوامر ولا ينفذها.
3. Prepare & Review: يجهز خطة وأوامر للمراجعة.
4. Execute with Approval: ينفذ بعد موافقة المستخدم.
5. Bounded Autonomous Execution: تنفيذ مستقل داخل حدود صريحة ومحددة.

المستوى الافتراضي هو المساعدة والمراجعة، وليس الاستقلال الكامل.

شكل الرد

كل اقتراح أمر مهم يجب أن يعرض:

• الهدف.
• الأمر المقترح.
• شرح مختصر.
• البيئة المستهدفة.
• Working Directory.
• الأثر المتوقع.
• المخاطر.
• الصلاحيات المطلوبة.
• هل يحتاج موافقة؟

لا يرسل المساعد أمرًا إلى Terminal بمجرد توليده.

17. T13 — Agents & Execution Runs

الهدف

متابعة أعمال الوكلاء التي تستخدم Terminal دون امتلاك دورة حياة الوكلاء نفسها.

Agent Runtime هو المالك لهوية الوكيل وحالته، وOrchestration هو المالك لتوزيع المهام والتنسيق.

Terminal يعرض فقط الأعمال المرتبطة بقدرات التنفيذ الخاصة به.

صفحة Execution Runs

تعرض:

• اسم المهمة.
• الوكيل المنفذ.
• الجلسة.
• البيئة.
• وقت البدء.
• الحالة.
• الخطوة الحالية.
• الصلاحيات المستخدمة.
• النتائج.
• Provenance.

تفاصيل التنفيذ

Plan

الخطة المعتمدة أو المقترحة.

Steps

الخطوات المنفذة والمتبقية.

Commands

الأوامر التي تم تشغيلها فعلًا.

Output

المخرجات.

Files

الملفات التي تأثرت إذا أمكن التحقق.

Approvals

الموافقات المطلوبة والممنوحة والمرفوضة.

Provenance

النماذج والمزودون والأدوار ومساهمات الوكلاء.

Review

النتيجة التي تحتاج مراجعة بشرية أو مستقلة.

التحكم

• Pause إذا كان مدعومًا.
• Request Stop.
• Resume وفق صلاحيات التنفيذ.
• Inspect.
• Review.
• Approve Next Action.
• Reject.
• Open Session.

لا يستطيع الوكيل توسيع صلاحياته بنفسه، ولا يعتبر نفسه الحكم الوحيد على اكتمال مهمته.

إدارة التنافس

عند عمل أكثر من وكيل:

• يجب منع التعارض غير المنسق على الموارد المشتركة.
• تستخدم آليات Claim & Lease المناسبة.
• تعرض الملكية المؤقتة للموارد.
• تظهر حالات الانتظار والتعارض.
• لا يطبق وكيل تغييرات متضاربة دون تنسيق.

18. T14 — Permissions & Security

الهدف

إعطاء المستخدم تحكمًا مفهومًا في حدود التنفيذ.

ملفات الصلاحيات

Read-Only

قراءة وفحص دون كتابة أو تنفيذ ذي أثر.

Restricted Execution

تنفيذ عمليات محددة منخفضة الأثر ضمن حدود واضحة.

Standard Execution

تنفيذ أوسع داخل بيئة ونطاق محددين.

Elevated / Extended

صلاحيات إضافية تحتاج موافقة صريحة وتوضيحًا للمخاطر.

هذه ملفات سياسات تصميمية؛ يجب ربطها بقيود تقنية فعلية، لا مجرد تسميات في الواجهة.

نطاق الصلاحيات

• Environment.
• Filesystem.
• Network.
• Processes.
• Installed Tools.
• Secrets Access.
• Remote Host.
• External Side Effects.

تصنيف المخاطر

• Low.
• Moderate.
• High.
• Critical.

التصنيف يجب أن يعتمد على طبيعة العملية والسياق، لا على اسم الأمر وحده.

Approval Request

كل طلب موافقة يعرض:

• من يطلب الإجراء.
• الأمر أو العملية كاملة.
• البيئة والمضيف.
• Working Directory.
• الملفات أو الموارد المتأثرة.
• الصلاحيات المطلوبة.
• الأثر المتوقع.
• قابلية التراجع.
• البيانات التي ستغادر الجهاز أو حدود الثقة.
• مدة الصلاحية.
• سبب الطلب.

الإجراءات:

• Approve.
• Reject.
• Review Details.

الموافقة على خطوة لا تعني الموافقة على كل الخطوات المستقبلية.

حماية الأوامر الحساسة

يجب التعامل بحذر مع:

• حذف الملفات والمجلدات.
• الكتابة فوق بيانات قائمة.
• تغيير الصلاحيات.
• أوامر إدارة النظام.
• التثبيت والتحديث.
• العمليات الشبكية الخارجية.
• تعديل إعدادات الأمان.
• التعامل مع بيانات الاعتماد.
• النشر أو النشر الخارجي.
• أوامر قد تسبب فقدانًا أو تعطيلًا للبيانات.

لا تعتمد الحماية على قائمة أسماء ثابتة فقط؛ يجب مراعاة سياق الأمر والبيئة والهدف والصلاحيات.

Secret Handling

• لا تعرض الأسرار في المخرجات إلا عند الضرورة المصرح بها.
• حاول تنقيح القيم الحساسة من السجلات.
• لا ترسلها إلى AI دون تفويض.
• لا تحفظها في Command History.
• لا تضعها في Snippets عامة.
• لا تسمح بنسخها تلقائيًا إلى الحافظة.

19. T15 — Logs & Diagnostics

الهدف

مركز موحد لفهم مشكلات Terminal والبيئات والاتصالات.

أنواع السجلات

• Session Events.
• Command Events.
• Process Events.
• Connection Events.
• Environment Health.
• AI Execution Events.
• Permission Events.
• System Diagnostics.

تفاصيل السجل

• الوقت.
• نوع الحدث.
• المصدر.
• الجلسة.
• البيئة.
• الحالة.
• الرسالة.
• الارتباط بالأمر أو العملية.
• Correlation ID عند توفره.

التشخيص

يمكن للمستخدم طلب:

• Explain Error.
• Analyze Failure.
• Compare Attempts.
• Suggest Repair.
• Generate Diagnostic Report.

مستويات العرض

• Simple Explanation.
• Detailed Technical View.
• Raw Logs.

يجب ألا تُخفى التفاصيل التقنية عن المستخدم المحترف، لكن تكون مخفية افتراضيًا للمبتدئ.

الخصوصية

يجب تنقيح الأسرار والبيانات الحساسة من السجلات قبل تخزينها أو تصديرها، وفق سياسة واضحة.

20. T16 — Terminal Settings

يجب أن تكون الإعدادات منظمة وبسيطة.

A. General

• Default Environment.
• Default Shell.
• Default Working Directory.
• Session Naming.
• Startup Behavior.
• Confirm Before Closing Active Session.

B. Appearance

• Terminal Theme.
• Font Family.
• Font Size.
• Line Height.
• Cursor Style.
• Cursor Blink.
• Color Scheme.
• Contrast.
• Selection Style.

C. Terminal Behavior

• Scrollback Limit.
• Word Wrap.
• Auto Scroll.
• Copy on Selection.
• Paste Confirmation.
• Clear View Behavior.
• Bell/Notifications.
• Input History.

D. Sessions

• Default Session Configuration.
• Restore Last Sessions.
• Session Retention.
• Auto Reconnect إذا كان مدعومًا.
• Session Naming Rules.

E. Environments

• Preferred Environment.
• Default Execution Mode.
• Resource Limits.
• Default Working Directory.
• Environment Notifications.

F. AI & Models

• AI Assistant Enabled.
• Preferred Model Policy.
• Context Sharing Controls.
• Explain/Suggest/Execute Defaults.
• AI Execution Limits.
• Provenance Visibility.

اختيار النموذج يخضع لنظام النماذج العام في Mimir، وليس إعدادًا منفصلًا يكرر مزودين أو بيانات اعتماد.

G. Autonomy & Permissions

• Default Permission Profile.
• Approval Rules.
• Autonomous Execution Limits.
• Network Access Policy.
• Filesystem Scope.
• Stop Conditions.

H. Keyboard & Shortcuts

• Command History.
• Clear View.
• Search Output.
• New Session.
• Split Pane.
• Switch Sessions.
• Open Command Palette.
• Focus AI Assistant.

I. Storage & History

• History Retention.
• Session Log Retention.
• Export Options.
• Privacy Controls.
• Clear History.

J. Security & Privacy

• Secret Redaction.
• Credential Storage.
• Permission Review.
• Active Connections.
• Security Events.

K. Advanced

• Shell Integration.
• Encoding.
• Environment Variables Policy.
• Debug Mode عند الدعم.
• Experimental Features إذا كانت موجودة ومعلنة بوضوح.

L. Reset

• Reset One Setting.
• Reset Category.
• Reset All Terminal Settings.

لا تؤثر إعادة الإعدادات على بيانات المستخدم أو الجلسات أو أسرار الاتصال.

21. T17 — Command Palette

الهدف

الوصول السريع لأي إجراء دون الحاجة للتنقل بين الصفحات.

فتحها

• زر في Header.
• اختصار لوحة المفاتيح.
• إجراء سياقي.

المجموعات

• Sessions.
• Commands.
• Environments.
• Processes.
• Navigation.
• AI Actions.
• Preferences.

أمثلة

• New Terminal Session.
• Switch Session.
• Split Terminal.
• Search Command History.
• Open Environment.
• Inspect Running Process.
• Explain Last Error.
• Open AI Assistant.
• Open Terminal Settings.

سلوكها

• بحث فوري.
• نتائج مرتبة حسب السياق.
• عرض الاختصار.
• توضيح إذا كان الإجراء غير متاح.
• عدم تنفيذ العمليات الحساسة من مجرد اختيار نتيجة.

الأوامر التي تنفذ آثارًا مهمة تحتاج خطوة مراجعة أو موافقة مستقلة.

22. تجربة المبتدئ والمحترف

22.1 Beginner Experience

يجب أن يتمكن المستخدم الذي لا يعرف البرمجة من:

• فهم وظيفة Terminal.
• اختيار بيئة مناسبة.
• فتح جلسة.
• وصف ما يريد فعله باللغة الطبيعية.
• رؤية الأمر قبل التنفيذ.
• فهم أثر الأمر.
• الموافقة أو الرفض.
• رؤية النتيجة بشرح واضح.
• استعادة العمل عند الفشل.

تُخفى التفاصيل المتقدمة افتراضيًا، لكن لا تُحذف من المنتج.

22.2 Professional Experience

المستخدم المحترف يحصل على:

• إدخال مباشر للأوامر.
• جلسات متعددة.
• Split Panes.
• Keyboard Shortcuts.
• History.
• Snippets.
• بحث داخل المخرجات.
• إدارة العمليات.
• تفاصيل البيئات.
• Logs خام.
• معلومات Execution Provenance.
• تحكم دقيق بالصلاحيات.
• إعدادات مستقلة.

22.3 مستويات العرض

ثلاثة مستويات:

1. Simple: شرح مختصر وإجراءات واضحة.
2. Standard: التفاصيل الأساسية مع تحكم أكبر.
3. Advanced: جميع التفاصيل التقنية والاختصارات والبيانات.

هذا لا يغير الصلاحيات أو حدود الأمان؛ بل يغير مقدار التفاصيل المعروضة.

23. التصميم حسب المنصة

23.1 Android Phone — الأولوية الأولى

يجب ألا نحاول تصغير واجهة Desktop ووضعها على الهاتف.

Terminal Home

• بطاقات مختصرة.
• جلسات نشطة.
• زر New Session واضح.
• ملخص البيئة.
• نشاط AI.

Terminal Workspace

• الطرفية هي المساحة الأساسية.
• Header مختصر.
• Session Tabs قابلة للتمرير.
• أدوات الطرفية في شريط سفلي سياقي.
• لوحة AI في Bottom Sheet أو Panel قابلة للفتح.
• Command Input فوق لوحة المفاتيح.
• الحفاظ على مساحة المخرجات قدر الإمكان.

لوحة المفاتيح

• يجب أن يظهر Command Input فوق لوحة المفاتيح.
• عدم إخفاء آخر المخرجات دون سبب.
• الحفاظ على موضع التمرير.
• دعم أزرار إضافية مفيدة للأوامر عند الحاجة.
• دعم النسخ واللصق وتحديد النص.
• لا يؤدي إغلاق لوحة المفاتيح إلى فقدان الإدخال.

Bottom Navigation

يبقى التنقل العام الخاص بـ Mimir كما هو. لا تنشأ Bottom Navigation ثانية تنافس التنقل العام.

داخل Terminal يمكن استخدام شريط إجراءات سياقي لا يحل محل Shell.

اللمس

• أهداف لمس واضحة.
• سحب لتغيير حجم اللوحات عند الدعم.
• تكبير وتصغير خط الطرفية.
• قوائم سياقية مناسبة.
• عدم الاعتماد على Hover.

قيود Android

لا نفترض أن كل جهاز Android يستطيع تشغيل Shell كامل أو Docker أو بيئة Linux أو جميع الأدوات.

يجب اكتشاف القدرات الحقيقية وتحديد ما هو:

• Local Native.
• Termux أو بيئة متوافقة عند توفر Adapter.
• Remote.
• Cloud.
• Hybrid.

لا تظهر ميزة غير مدعومة كأنها متاحة لمجرد وجود تصميم لها.

23.2 Tablet

• Navigation Rail أو Drawer تكيفي.
• Terminal Canvas أكبر.
• Session List جانبية.
• AI Panel مستقلة عند توفر المساحة.
• Split Terminal.
• File Explorer جانبي.
• إعادة توزيع اللوحات دون ازدحام.

23.3 Desktop / Web

التصميم الكامل:

• Navigation Sidebar.
• Workspace Header.
• Session Tabs.
• Terminal Canvas.
• Optional AI Side Panel.
• Optional Files/Processes Panel.
• Resizable Panes.
• Command Palette.
• Keyboard Shortcuts.
• Context Menus.
• Multi-session Layout.

يجب حفظ توزيع اللوحات لكل مساحة عمل واستعادته دون تغيير حالة التنفيذ.

24. التكامل مع Forge

التكامل لا يعني دمج المنتجين.

السيناريوهات

• Forge يطلب تشغيل Build Command.
• Forge يطلب تنفيذ Test Command.
• Forge يستدعي Terminal لفحص بيئة.
• المستخدم يفتح مسارًا في Forge من Terminal.
• المستخدم يفتح جلسة مرتبطة بمشروع.
• AI في Forge يطلب تنفيذًا عبر عقد عام.

قواعد التكامل

• Terminal يملك التنفيذ.
• Forge يملك سير العمل الهندسي.
• Forge لا ينشئ Terminal بديلًا.
• Terminal لا يدير Code Review أو Releases.
• الصلاحيات تُطبق من الجهات المالكة.
• Model Provenance يظل موحدًا.
• كل تنفيذ يحتفظ برابط إلى المهمة أو المشروع الأصلي.

25. Model Provenance & Execution Transparency

يجب أن يطبق عقد Mimir العام داخل Terminal.

لكل نتيجة مولدة بواسطة نموذج أو وكيل، يجب الاحتفاظ متى توفرت البيانات بـ:

• Model Identity.
• Model Version.
• Provider.
• Local / Cloud / Hybrid.
• Role.
• Contribution.
• Start Timestamp.
• Completion Timestamp.
• Execution Status.
• Participating Models.
• Final Synthesizer عند تعدد النماذج.
• Handoff Relationship.
• Source References عند وجودها.

بالنسبة للأوامر:

• Command Author: User / AI / Agent / Workflow.
• Executor.
• Session.
• Environment.
• Execution ID.
• Result.
• Exit Code عند توفره.
• Approval Reference.
• Related Task.

لا ينسب الأمر الذي كتبه المستخدم إلى AI.

لا تختفي بيانات Provenance عند نقل العمل أو المزامنة أو التصدير أو استعادة الجلسة.

26. حالات الاتصال والعمل دون اتصال

يجب التمييز بين:

• Online.
• Offline.
• Connecting.
• Reconnecting.
• Cloud Unavailable.
• Remote Host Unavailable.
• Local Available / Remote Unavailable.
• Syncing.

لا يعني Offline أن Terminal يستطيع تنفيذ أوامر محلية؛ يجب التحقق من وجود بيئة محلية صالحة.

إذا لم يكن التنفيذ متاحًا:

• يمكن استعراض History المحفوظ.
• يمكن قراءة Snippets.
• يمكن إعداد أوامر دون تنفيذ.
• يمكن مراجعة جلسات سابقة.
• لا تعرض الواجهة تنفيذًا وهميًا.

27. حالات الخطأ والتعافي

أمثلة

• Shell Unavailable.
• Session Creation Failed.
• Connection Timeout.
• Authentication Failure.
• Permission Denied.
• Environment Missing.
• Command Failed.
• Process Termination Failed.
• Storage Failure.
• Unknown Execution Outcome.

قواعد التعافي

• حفظ الإدخال إذا كان آمنًا.
• إظهار ما إذا كان الأمر بدأ أم لا.
• عدم إعادة تنفيذ عملية خارجية عند غموض نتيجتها.
• إظهار سبب الفشل بوضوح.
• توفير Retry عندما يكون آمنًا.
• توفير Reconnect عند الحاجة.
• الاحتفاظ بالأدلة والسجلات.
• عدم إظهار نجاح غير مؤكد.

28. الخصوصية والاحتفاظ بالبيانات

يجب تحديد سياسات واضحة لـ:

• Command History.
• Session Logs.
• AI Context.
• Execution Outputs.
• Connection Metadata.
• Secrets.
• Diagnostic Events.

قواعد أساسية:

1. لا تحفظ الأسرار ضمن History.
2. لا ترسل بيانات البيئة إلى AI إلا وفق التفويض.
3. لا تسجل قيمًا حساسة في Logs.
4. يمكن للمستخدم تحديد مدد الاحتفاظ حيث تدعمها المنصة.
5. التصدير يحترم الصلاحيات وينبه إلى البيانات الحساسة.
6. الحذف لا يُعلن اكتماله نهائيًا قبل التحقق من التخزين والنسخ الاحتياطية.
7. سجلات التدقيق لا تتحول إلى وسيلة لتسريب محتوى المستخدم.

29. الإشعارات

أمثلة الإشعارات:

• Session Started.
• Command Completed.
• Command Failed.
• Process Needs Attention.
• Connection Lost.
• Agent Needs Approval.
• Agent Completed.
• Environment Unavailable.

يجب أن تكون الإشعارات قابلة للتحكم، وألا تعرض محتوى حساسًا على شاشة القفل افتراضيًا.

30. الأداء والاستجابة

المتطلبات التصميمية:

• استجابة سريعة لفتح الجلسات.
• تحديث المخرجات دون تجميد الواجهة.
• عدم فقدان النص عند وصول مخرجات كثيفة.
• إدارة Scrollback بكفاءة.
• فصل عرض المخرجات عن عمليات التنفيذ.
• عدم تعطيل الواجهة بسبب عملية طويلة.
• دعم الجلسات الخلفية وفق قدرات البيئة.
• تحميل تفاصيل السجلات عند الطلب.
• عدم تحميل جميع بيانات History دفعة واحدة.

أي أرقام أداء مستهدفة يجب تحديدها لاحقًا بناءً على الأجهزة والبيئات الفعلية والاختبارات.

31. Accessibility وRTL/LTR

• دعم قارئات الشاشة.
• أسماء واضحة لكل زر وأيقونة.
• ترتيب Focus منطقي.
• دعم لوحة المفاتيح على Desktop.
• دعم Dynamic Text Scaling للواجهة.
• عدم تغيير محاذاة الأوامر البرمجية تلقائيًا بسبب RTL.
• عزل النصوص البرمجية والمسارات وعناوين URL في اتجاه LTR عند الحاجة.
• الحفاظ على ترتيب الحروف والرموز في الأوامر.
• تباين واضح.
• دعم Reduced Motion.
• عدم الاعتماد على اللون وحده لتوضيح حالة التنفيذ.

32. حالات التصميم المطلوبة لكل صفحة

يجب ألا يُصمم أي Screen للحالة الطبيعية فقط.

لكل صفحة يجب تحديد:

• Default.
• Loading.
• Empty.
• Error.
• Offline.
• Permission Denied.
• Unavailable Capability.
• Partial Data.
• Success.
• Confirmation.
• Destructive Action.
• Restored State.

كما يجب توثيق:

• Mobile.
• Tablet.
• Desktop.
• Light.
• Dark.
• RTL.
• LTR.
• Accessibility.

33. معايير قبول التصميم

المنتج

[  ]  Terminal مساحة مستقلة عن Forge.
[  ]  Terminal يملك واجهته وجلساته وعمليات التنفيذ الخاصة به.
[  ]  لا يكرر الخدمات العامة.
[  ]  لا يعتمد Kernel على معرف Terminal أو شروط خاصة به.
[  ]  كل قدرة معلنة تتطابق مع قدرات البيئة الحقيقية.

الصفحات

[  ]  الصفحات T01 إلى T17 موثقة.
[  ]  لكل صفحة هدف واضح.
[  ]  لكل صفحة إجراءات وحالات.
[  ]  لا توجد وجهات مكررة تؤدي الوظيفة نفسها.
[  ]  التنقل متسق مع Application Shell.

Terminal Workspace

[  ]  الطرفية هي مركز العمل.
[  ]  جلسات متعددة.
[  ]  إدخال أوامر ومخرجات.
[  ]  بحث ونسخ وتحديد.
[  ]  Split View عند الدعم.
[  ]  العمليات الطويلة لا تفقد عند التنقل.
[  ]  حالات التنفيذ صادقة.

AI & Agents

[  ]  المساعد يفهم السياق المصرح به.
[  ]  يعرض الأمر قبل التنفيذ عند الحاجة.
[  ]  يوضح الأثر والمخاطر.
[  ]  لا يتجاوز الصلاحيات.
[  ]  يميز الاقتراح عن التنفيذ.
[  ]  الوكلاء لا يوسعون صلاحياتهم.
[  ]  كل مساهمة تحفظ Provenance.

Security

[  ]  صلاحيات البيئة محددة.
[  ]  الموافقات توضح الأثر.
[  ]  الأسرار محمية.
[  ]  السجلات منقحة.
[  ]  العمليات الحساسة تحتاج موافقة مناسبة.
[  ]  لا توجد صلاحيات ضمنية بسبب فتح جلسة.

Responsive

[  ]  Android Phone.
[  ]  Tablet.
[  ]  Desktop/Web.
[  ]  RTL/LTR.
[  ]  Keyboard/Safe Areas.
[  ]  Touch Targets.
[  ]  Accessibility.

34. ما لا ينبغي ادعاء دعمه دون تحقق

هذه القائمة إلزامية:

• تشغيل Shell محلي على كل أجهزة Android.
• دعم كل توزيعات Linux.
• Docker أو Containers.
• SSH أو Remote Hosts.
• Cloud Execution.
• Background Processes.
• Session Persistence.
• Process Pause/Resume.
• Secure Sandbox.
• Resource Monitoring.
• Package Installation.
• Network Isolation.
• File System Isolation.
• Auto Reconnect.
• Multi-Agent Execution.

كل ميزة يجب أن تكون مرتبطة بمزود أو Adapter قادر على تنفيذها، وباختبارات تثبت سلوكها.

إذا كانت الميزة غير متاحة، تعرض Disabled أو Unsupported أو Planned حسب حالتها الحقيقية.

35. مراحل تحويل التصميم إلى منتج

المرحلة A — Foundation

• Terminal Capability Manifest.
• Execution Contracts.
• Session Contracts.
• Environment Contracts.
• Permission Integration.
• Provenance Integration.
• Result/Error Semantics.

المرحلة B — Core Terminal

• Terminal Home.
• Sessions.
• Terminal Workspace.
• Command Input/Output.
• History.
• Keyboard & Touch Controls.

المرحلة C — Environment Management

• Environments.
• Environment Details.
• Connections.
• File & Path Explorer.
• Processes & Jobs.

المرحلة D — AI Experience

• AI Terminal Assistant.
• Command Explanation.
• Command Planning.
• Diagnostics.
• Agent Execution Monitoring.

المرحلة E — Governance & Operations

• Permissions.
• Approvals.
• Logs.
• Diagnostics.
• Retention.
• Security Review.

المرحلة F — Advanced UX

• Command Palette.
• Snippets.
• Multi-pane.
• Advanced Settings.
• Responsive Refinement.
• Accessibility.
• Performance.

هذا ترتيب مقترح للتنفيذ، وليس ادعاءً بأن أي مرحلة بدأت أو اكتملت.

36. القرار التصميمي النهائي

Mimir Terminal يجب أن يكون:

• Terminal احترافيًا حقيقيًا، وليس محاكاة شكلية.
• سهلًا للمبتدئ دون إضعاف قدرات المحترف.
• AI-native دون أن يصبح AI-only.
• قادرًا على التعامل مع جلسات وبيئات متعددة بحسب الدعم الفعلي.
• مستقلًا عن Forge ومتكاملًا معه.
• خاضعًا لنظام الصلاحيات والموافقات الموحد.
• شفافًا في مصدر الأوامر والنماذج والتنفيذ.
• مصممًا للهاتف أولًا، مع تجربة Desktop كاملة.
• قابلًا للتوسع عبر العقود والـAdapters دون ربط Kernel بتفاصيله.

المبدأ النهائي:

المستخدم يستطيع أن يشرح ما يريد بلغة طبيعية، أو يعمل مباشرة بالأوامر، أو يفوض وكيلًا لتنفيذ مهمة محددة؛ وفي جميع الحالات يحتفظ بالقدرة على الفهم والمراجعة والتحكم والإيقاف، ويستطيع معرفة ما نُفّذ فعلًا وما لم يُنفّذ.

───

37. حالة الوثيقة

هذه الوثيقة تمثل التصور الشامل المقترح لتصميم Mimir Terminal وصفحاته ووظائفه وتفاعلاته.

• حالة التصميم: Comprehensive Draft.
• Document Registry: Pending.
• Architecture Reconciliation: Required.
• UI Implementation: Not verified.
• Runtime Implementation: Not verified.
• Security Validation: Pending.
• Responsive Validation: Pending.
• Accessibility Validation: Pending.

لا تُعتبر أي ميزة منفذة أو مختبرة لمجرد ورودها في هذه المواصفة.
