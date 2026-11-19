───

document_id: PENDING_DOCUMENT_REGISTRY
title: Mimir Terminal — Screen-by-Screen UX & Interaction Specification
document_type: Workspace Screen Specification
status: Comprehensive Design Draft — Pending Review and Registry Approval
parent_specification: Mimir Terminal — Comprehensive Product, UX & Interaction Specification
related_documents:

• MIMIR-UX-SHELL-001
• MIMIR-UX-INT-001
• MIMIR-UX-SCREEN-001
• MIMIR-ENG-FORGE-001
• MIMIR-UX-DTC-001
• MIMIR-UX-CLS-001

───

Mimir Terminal

Screen-by-Screen UX & Interaction Specification

1. Purpose

هذه الوثيقة تحول التصور الشامل لـ Mimir Terminal إلى تصميم تفصيلي قابل للمراجعة والتنفيذ لاحقًا.

تحدد:

• شكل كل صفحة ومناطقها.
• ترتيب المعلومات.
• الأزرار والإجراءات.
• التنقل بين الصفحات.
• سلوك التفاعل.
• حالات الصفحة.
• الاختلاف بين الهاتف والتابلت وسطح المكتب.
• علاقة كل صفحة بالخدمات المشتركة.
• ما يراه المستخدم المبتدئ وما يستطيع المحترف الوصول إليه.

هذه الوثيقة مكملة للمواصفة الشاملة، وليست بديلًا عنها.

2. قواعد التصميم الملزمة

2.1 استقلال المساحات

• Terminal مسؤول عن التفاعل الطرفي وتنفيذ الأوامر والعمليات المرتبطة بجلساته.
• Forge مسؤول عن دورة حياة هندسة البرمجيات.
• لا يتحول Terminal إلى محرر كود أو مدير مشاريع.
• يمكن لـ Forge استدعاء التنفيذ من خلال عقد عام.
• يمكن للمستخدم الانتقال بين Terminal وForge مع الحفاظ على مرجع المشروع والجلسة عند وجودهما.

2.2 الخدمات المشتركة

الوظيفة| الجهة المالكة
التنقل العام| Application Shell
Composer العام| Application Shell
هوية المستخدم| Identity Service
الصلاحيات والموافقات| Permissions & Approval Services
المهام| Task Service
الوكلاء| Agent Runtime
التنسيق| Orchestration
البحث العام| Unified Search
النشاط والتدقيق| Activity & Audit
الأسرار| Secrets Vault
الملفات| Mimir Files
المعرفة والذاكرة| Mimir Brain
Provenance| Global Provenance Contract
الاتصال والمزامنة| Shared Connectivity & Sync

Terminal يعرض المعلومات ذات الصلة بهذه الخدمات ويستخدم عقودها؛ ولا ينشئ نسخًا موازية منها.

2.3 قواعد حالات التنفيذ

يجب عرض كل نوع حالة بصورة مستقلة:

• Session State.
• Process State.
• Task State.
• Agent Run State.
• Tool Invocation State.
• Connection State.
• Environment Health.

لا تستخدم شارة واحدة باسم Running لتصف جميع هذه الكيانات دون تحديد نوعها.

2.4 القواعد البصرية

• استخدام Design Tokens المعتمدة.
• الحفاظ على شخصية Mimir Balanced.
• دعم Pearl Light وDark.
• استخدام Monospace داخل الطرفية.
• عدم استخدام Glass قوي خلف الأوامر أو النصوص التقنية الكثيفة.
• تباين واضح بين الأمر والمخرجات والأخطاء.
• لا تتغير ألوان الحالة وحدها دون نص أو رمز توضيحي.
• تكبير خط الطرفية مستقل عن خط واجهة Mimir.

3. خريطة الصفحات النهائية

الرمز| الصفحة| نوع الظهور
T01| Terminal Home| صفحة رئيسية
T02| Sessions| صفحة قائمة
T03| Terminal Workspace| مساحة عمل أساسية
T04| Session Details| صفحة تفاصيل
T05| Command History| صفحة قائمة وبحث
T06| Commands & Snippets| مكتبة
T07| Environments| صفحة قائمة
T08| Environment Details| صفحة تفاصيل
T09| File & Path Explorer| مستكشف سياقي
T10| Processes & Jobs| صفحة إدارة
T11| Connections & Hosts| صفحة إدارة
T12| AI Terminal Assistant| لوحة سياقية
T13| Agents & Execution Runs| صفحة متابعة
T14| Permissions & Security| صفحة عرض أمني مرتبطة بالخدمات العامة
T15| Logs & Diagnostics| صفحة تشخيص
T16| Terminal Settings| صفحة إعدادات
T17| Command Palette| Overlay سريع

الأرقام T01–T17 معرفات داخلية لتنظيم التصميم، وليست Document IDs رسمية.

4. الإطار المشترك لصفحات Terminal

4.1 Header

يحتوي حسب الصفحة على:

• عنوان الصفحة.
• اسم مساحة Terminal.
• اسم الجلسة أو البيئة عند وجود سياق محدد.
• حالة الاتصال ذات الصلة.
• إجراءات الصفحة الأساسية.
• قائمة More.

لا يعرض Header معلومات لا تخص الصفحة الحالية.

4.2 Workspace Navigation

التنقل داخل Terminal يستخدم Top Tabs أو قائمة سياقية بحسب حجم الشاشة.

التبويبات الأساسية:

• Overview.
• Sessions.
• Environments.
• Processes.
• Commands.
• Connections.
• History.

لا يتم وضع هذه التبويبات في Bottom Navigation العام على الهاتف؛ فهي تنقل داخلي خاص بالمساحة.

4.3 Context Bar

يظهر عند وجود جلسة أو بيئة نشطة.

يعرض:

• البيئة.
• Shell.
• Working Directory.
• موقع التنفيذ.
• حالة الاتصال.
• مستوى الصلاحيات.

يمكن ضغطه لفتح التفاصيل.

4.4 Contextual AI Panel

لوحة مساعدة تظهر عند طلب المستخدم أو عند وجود مساحة كافية.

تحتوي على:

• شرح.
• اقتراح.
• تشخيص.
• خطة.
• مراجعة أمر.
• متابعة تنفيذ.

لا تنشئ Composer مستقلًا؛ تستخدم Composer العام الموجود في Application Shell.

4.5 Page States

كل صفحة يجب أن تدعم:

1. Default.
2. Loading.
3. Empty.
4. Error.
5. Offline.
6. Partial Data.
7. Permission Denied.
8. Unsupported Capability.
9. Confirmation.
10. Restored State.

───

5. T01 — Terminal Home

الهدف

إعطاء المستخدم نقطة دخول واضحة لمعرفة ما يحدث داخل Terminal وبدء العمل بسرعة.

ترتيب الصفحة

المنطقة A — Workspace Header

• Terminal.
• البيئة الافتراضية الحالية.
• حالة البيئة.
• زر New Session.
• قائمة المزيد.

المنطقة B — Quick Actions

ترتيب الإجراءات:

1. New Session.
2. Open Recent Session.
3. Select Environment.
4. Connect to Host.
5. Ask AI.
6. Browse History.

يظهر الإجراء فقط عندما تكون قدرته متاحة.

المنطقة C — Active Sessions

بطاقات الجلسات النشطة.

كل بطاقة تحتوي:

• اسم الجلسة.
• اسم البيئة.
• Shell.
• الحالة.
• آخر نشاط.
• مؤشر وجود عملية نشطة.
• زر Open.

عند الضغط على البطاقة يفتح Terminal Workspace.

المنطقة D — Running Processes

قائمة مختصرة بالعمليات التي تحتاج انتباهًا.

تعرض:

• اسم العملية.
• الجلسة المرتبطة.
• البيئة.
• الحالة.
• المدة.

زر View All ينقل إلى Processes & Jobs.

المنطقة E — Environments

بطاقات البيئات:

• الاسم.
• النوع.
• الحالة.
• آخر تحقق.
• عدد الجلسات المرتبطة.

المنطقة F — AI Activity

تعرض ملخصًا للمهام التي يستخدم فيها AI قدرات Terminal:

• قيد التنفيذ.
• تنتظر موافقة.
• اكتملت.
• فشلت.

لا تعرض كل تفاصيل الوكلاء في الصفحة الرئيسية.

المنطقة G — Recent Activity

تعرض أحدث الأحداث المرتبطة بـ Terminal، مع رابط إلى Activity العامة عند الحاجة.

التفاعل

• New Session يفتح Session Creation Flow.
• Open Recent يفتح الجلسة المحددة.
• Select Environment يفتح Environment Picker.
• Ask AI يفتح Composer العام مع سياق Terminal.
• View All ينقل إلى الصفحة المعنية.

الهاتف

• بطاقات رأسية.
• Quick Actions مختصرة.
• الجلسات النشطة قبل السجلات.
• عدم وضع أكثر من لوحة تفصيلية مفتوحة في الوقت نفسه.

التابلت

• Active Sessions وEnvironments في عمودين.
• Recent Activity وAI Activity أسفلها.

Desktop

• لوحة رئيسية متعددة الأعمدة.
• Sessions وProcesses في مساحة بارزة.
• Environment Overview في الجانب.
• Activity في الأسفل.

الحالات الخاصة

First Use: شرح قصير عن Terminal وزر إنشاء أول جلسة.

No Environment: عرض طريقة إضافة بيئة أو اختيار بيئة مدعومة.

No Active Sessions: إظهار زر بدء جلسة.

Offline: توضيح ما هو متاح محليًا وما يتطلب اتصالًا.

───

6. T02 — Sessions

الهدف

إدارة الجلسات المفتوحة والسابقة.

Header

• Sessions.
• Search.
• Filter.
• New Session.

شريط الفلاتر

• All.
• Active.
• Waiting.
• Disconnected.
• Completed.
• Archived.

يمكن التصفية أيضًا حسب البيئة.

بطاقة الجلسة

الترتيب:

1. Session Name.
2. Environment.
3. Shell.
4. Working Directory.
5. Session State.
6. Active Process Indicator.
7. Last Activity.
8. More.

قائمة الإجراءات

• Open.
• Rename.
• Pin.
• Inspect.
• Archive.
• Close.

لا يوضع Terminate في قائمة Close؛ إنه إجراء مختلف يتعلق بالعملية أو الجلسة بحسب عقد التنفيذ.

New Session Flow

يفتح Wizard:

Step 1 — Environment
اختيار البيئة.

Step 2 — Shell
اختيار Shell متاح فعليًا.

Step 3 — Working Directory
اختيار مجلد البداية.

Step 4 — Permissions
مراجعة حدود الصلاحيات.

Step 5 — Review
ملخص الجلسة قبل الإنشاء.

Step 6 — Create
إنشاء الجلسة ثم فتح Workspace.

حالات الصفحة

• No Sessions.
• Loading Sessions.
• Session Starting.
• Session Creation Failed.
• Partial Session List.
• Permission Restricted.

الهاتف

قائمة بطاقات رأسية، مع قائمة إجراءات داخل كل بطاقة.

Desktop

جدول أو قائمة كثيفة مع إمكانية الترتيب حسب الاسم والحالة والنشاط.

───

7. T03 — Terminal Workspace

الهدف

هذه هي مساحة العمل الأساسية التي يقضي المستخدم معظم وقته فيها.

7.1 التكوين على Desktop

┌─────────────────────────────────────────────────────────────┐
│ Terminal / Session / Environment / Connection / More │
├─────────────────────────────────────────────────────────────┤
│ Session Tabs │
├───────────────────────────────┬─────────────────────────────┤
│ │ │
│ │ Contextual Panel │
│ Terminal Canvas │ AI / Files / Processes │
│ │ Session Details │
│ │ │
├───────────────────────────────┴─────────────────────────────┤
│ Command Input │
└─────────────────────────────────────────────────────────────┘

هذا مخطط وظيفي يوضح العلاقات وليس قياسات نهائية.

7.2 Header

يعرض:

• اسم الجلسة.
• البيئة.
• Shell.
• Working Directory.
• الاتصال.
• حالة الجلسة.

الإجراءات:

• Session Details.
• Split View.
• Context Panel.
• Command Palette.
• More.

7.3 Session Tabs

كل تبويب يعرض:

• اسم الجلسة.
• حالة الجلسة.
• مؤشر عملية نشطة.
• علامة تنبيه عند وجود خطأ مهم.

عند إغلاق Tab:

• لا يتم افتراض إيقاف العملية.
• يظهر تحذير إذا كان الإغلاق سيؤدي إلى فقدان حالة غير محفوظة.
• يظل العمل الخلفي مستمرًا وفق سياسة البيئة.

7.4 Terminal Canvas

منطقة عرض الطرفية:

• Prompt.
• Command Input.
• Output.
• Error Output.
• Scrollback.
• Selection.
• Search in Output.

تُعرض المخرجات بالتتابع مع الحفاظ على ترتيبها.

تمييز الأنواع

• Command: نص الأمر الذي أرسله المستخدم أو الوكيل.
• Standard Output: المخرجات العادية.
• Standard Error: رسائل الخطأ.
• System Event: أحداث حالة التنفيذ.
• AI Annotation: شرح أو تعليق من AI، وليس جزءًا من مخرجات Shell الأصلية.

يجب ألا تختلط مخرجات AI بالمخرجات الأصلية دون تمييز.

7.5 Command Input

يحتوي على:

• حقل الإدخال.
• زر تنفيذ.
• اقتراحات عند توفرها.
• زر فتح History.
• زر إظهار اختصارات الإدخال.

قبل التنفيذ، يجب أن يكون واضحًا:

• الأمر.
• البيئة المستهدفة.
• الجلسة.
• Working Directory.

إذا كان الأمر عالي التأثير، تظهر مراجعة وموافقة مناسبة قبل الإرسال.

7.6 Running Command Bar

عند وجود عملية قيد التنفيذ:

• اسم الأمر.
• الحالة.
• المدة.
• مؤشر وصول المخرجات.
• زر فتح التفاصيل.
• Stop أو Cancel بحسب نوع الكيان ودعم عقد التنفيذ.

لا يظهر زر Pause إلا إذا كانت العملية تدعمه.

7.7 Contextual Side Panel

التبويبات:

• AI.
• Session.
• Files.
• Processes.
• Command Details.

يمكن إغلاق اللوحة دون التأثير على العملية.

7.8 Context Menu

عند تحديد أمر أو نتيجة:

• Copy.
• Search.
• Explain.
• Save as Snippet.
• Inspect.
• Run Again عند صلاحية المستخدم.

Run Again يعرض الأمر والبيئة قبل التنفيذ.

7.9 الهاتف

الوضع الافتراضي

• Header مختصر.
• Session Selector.
• Terminal Canvas بأكبر مساحة ممكنة.
• Command Input ثابت فوق لوحة المفاتيح.
• شريط إجراءات سياقي.

لوحة الإجراءات

تتضمن:

• History.
• AI.
• Files.
• Processes.
• Session Details.
• More.

لوحة المفاتيح

• يتحرك Command Input فوق لوحة المفاتيح.
• لا يغطي آخر المخرجات.
• يحافظ على موضع التمرير.
• لا يفقد النص غير المرسل.
• يمكن إغلاق لوحة المفاتيح دون إنهاء الجلسة.

7.10 التابلت

• Terminal Canvas في المساحة الرئيسية.
• Session List قابلة للطي.
• AI Panel جانبية.
• إمكانية Split عند توفر المساحة.

7.11 الحالات

• Session Initializing.
• Ready.
• Running.
• Waiting for Input.
• Connection Lost.
• Output Interrupted.
• Command Failed.
• Unknown Outcome.
• Session Restored.

───

8. T04 — Session Details

Header

• اسم الجلسة.
• الحالة.
• More.

تبويبات التفاصيل

Overview

• Session ID.
• Environment.
• Shell.
• Working Directory.
• Owner.
• Created At.
• Last Activity.

Configuration

• إعدادات الجلسة.
• Encoding.
• Terminal Size.
• Scrollback.
• Startup Configuration.

Activity

أحداث الجلسة المرتبطة بـ Activity & Audit العامة.

Processes

العمليات المرتبطة بالجلسة.

Provenance

مصدر الأوامر ومعلومات النماذج والوكلاء عند وجودها.

الإجراءات

• Rename.
• Reconnect.
• Archive.
• Close.
• Export Session Log.

إجراء إيقاف عملية مرتبطة يظهر في سياق العملية وليس باعتباره Close Session.

الهاتف

تفاصيل في أقسام قابلة للطي.

Desktop

Tabs جانبية أو علوية مع جدول الأحداث.

───

9. T05 — Command History

Header

• Command History.
• Search.
• Filters.
• Export عند توفر الصلاحية.

Search

يدعم البحث في:

• نص الأمر.
• اسم الجلسة.
• البيئة.
• Working Directory.
• نتيجة التنفيذ.

Filters

• Time.
• Session.
• Environment.
• User / Agent.
• Status.
• Execution Type.

Command Row

• الأمر.
• وقت التنفيذ.
• الجلسة.
• البيئة.
• الحالة.
• المدة.
• مصدر التنفيذ.

Command Details

عند فتح عنصر:

1. الأمر الكامل.
2. المخرجات.
3. الأخطاء.
4. مدة التنفيذ.
5. Exit Code إذا توفر.
6. البيئة.
7. الجلسة.
8. Execution ID.
9. Approval Reference.
10. Provenance.

الإجراءات

• Copy.
• Explain.
• Save Snippet.
• Run Again.
• Export.
• Open Session.

لا يُعاد تنفيذ أمر تلقائيًا بمجرد فتح تفاصيله.

حالات الصفحة

• Empty History.
• Search No Results.
• Partial History.
• History Unavailable.
• Restricted History.

───

10. T06 — Commands & Snippets

الصفحة الرئيسية للمكتبة

Header

• Commands & Snippets.
• Search.
• New Snippet.

Categories

• Favorites.
• Recent.
• Personal.
• Shared عند الدعم.
• System Templates.

Snippet Card

• الاسم.
• الوصف.
• Shell Compatibility.
• التصنيف.
• آخر تعديل.
• مستوى المخاطر.

Snippet Details

• Description.
• Command Template.
• Parameters.
• Required Environment.
• Required Permissions.
• Risk Classification.
• Compatibility.

Snippet Builder

خطوات الإنشاء:

1. Name.
2. Description.
3. Command Template.
4. Parameters.
5. Compatibility.
6. Risk Review.
7. Preview.
8. Save.

AI Snippet Builder

يصف المستخدم المطلوب باللغة الطبيعية.

يعرض AI:

• الأمر.
• الشرح.
• المتغيرات.
• البيئة المتوافقة.
• المخاطر.
• الأثر المتوقع.

ثم يختار المستخدم:

• Save.
• Edit.
• Discard.
• Prepare for Execution.

لا ينفذ Snippet تلقائيًا بعد إنشائه.

حالات الصفحة

• Empty Library.
• Search Empty.
• Incompatible Shell.
• Missing Parameter.
• Unsupported Environment.

───

11. T07 — Environments

Header

• Environments.
• Search.
• Add Environment.

Environment Summary

• Local.
• Remote.
• Cloud.
• Container.
• Sandbox.

لا تظهر الفئة إذا لم يكن لها دعم فعلي.

Environment Card

• الاسم.
• النوع.
• المزود.
• الحالة.
• آخر Health Check.
• عدد الجلسات.
• Capabilities Summary.

Filters

• All.
• Available.
• Unavailable.
• Local.
• Remote.
• Cloud.
• Project Scoped.

Add Environment Flow

Step 1 — Type

اختيار نوع البيئة.

Step 2 — Provider

اختيار مزود متاح.

Step 3 — Identity

تحديد بيانات الاتصال المطلوبة.

Step 4 — Configuration

إعدادات التشغيل.

Step 5 — Permissions

مراجعة الصلاحيات.

Step 6 — Test

اختبار الاتصال والقدرات عند دعم ذلك.

Step 7 — Review

عرض ملخص.

Step 8 — Create

إنشاء البيئة.

الحالات

• No Environments.
• Provider Unavailable.
• Capability Discovery.
• Connection Failed.
• Environment Ready.
• Partial Capability.

الهاتف

قائمة بطاقات مع Wizard متتابع.

Desktop

بطاقات أو جدول مع لوحة تفاصيل جانبية.

───

12. T08 — Environment Details

Header

• اسم البيئة.
• الحالة.
• More.

Tabs

Overview

النوع، الموقع، المزود، آخر فحص.

Capabilities

القدرات المكتشفة فعليًا:

• Shell.
• File Access.
• Process Control.
• Network Access.
• Persistent Storage.
• Background Execution.
• Session Restore.

Tools

الأدوات وإصداراتها عند توفر التحقق.

Resources

الموارد المتاحة فعليًا.

Sessions

الجلسات المرتبطة.

Security

نطاق الوصول والصلاحيات.

Activity

الأحداث المرتبطة.

الإجراءات

• Health Check.
• Refresh Capabilities.
• Reconnect.
• Edit Configuration.
• Disable.
• Remove.

قبل Remove:

• عرض الجلسات المرتبطة.
• عرض العمليات التي قد تتأثر.
• عرض البيانات التي قد تفقد.
• طلب تأكيد واضح.

لا يتم إظهار أن البيئة أزيلت نهائيًا قبل تأكيد الجهة المالكة.

───

13. T09 — File & Path Explorer

الهدف

استكشاف المسارات المتاحة لبيئة Terminal دون تكرار Mimir Files.

Header

• Environment.
• Current Path.
• Search.
• Refresh.

Path Navigation

• Breadcrumb.
• Parent Folder.
• Current Directory.

File List

• Name.
• Type.
• Size عند توفره.
• Last Modified عند توفره.
• Access Status.

File Details

• Full Path.
• Type.
• Size.
• Environment.
• Permission.
• Last Modified.

Actions

• Open.
• Copy Path.
• Reveal Parent.
• Search.
• Explain with AI.
• Open in Forge عند ارتباطه بمشروع هندسي.

Write/Delete

لا تظهر هذه الإجراءات كأزرار عامة سريعة.

عند دعمها، يجب:

1. عرض المسار.
2. عرض الهدف.
3. توضيح الأثر.
4. التحقق من الصلاحية.
5. طلب الموافقة المناسبة.

التكامل

Mimir Files يظل مالك تجربة إدارة الملفات العامة.

Terminal يعرض المسارات المرتبطة ببيئة التنفيذ ويستخدم خدمات الملفات عند الحاجة.

الهاتف

قائمة ملفات بسيطة مع Breadcrumb قابل للتمرير.

Desktop

Explorer جانبي مع Preview عند الدعم.

───

14. T10 — Processes & Jobs

Header

• Processes & Jobs.
• Search.
• Filters.

Summary

• Running.
• Waiting.
• Completed.
• Failed.
• Needs Attention.

Process Table / Cards

• Process.
• Session.
• Environment.
• Started At.
• Duration.
• State.
• Resource Usage عند توفره.

Process Details

• الأمر الأصلي.
• معرف العملية.
• الجلسة.
• البيئة.
• Parent Process عند توفره.
• المخرجات.
• الحالة.
• سجل الأحداث.

Controls

• Inspect.
• Open Session.
• View Output.
• Request Stop.
• Terminate إذا كان مدعومًا ومصرحًا.

Confirmation

قبل الإنهاء تعرض:

• اسم العملية.
• الأمر.
• البيئة.
• الجلسة.
• الأثر المتوقع.
• العمليات التابعة إن أمكن معرفتها.

حالات الصفحة

• No Processes.
• Process Running.
• Waiting.
• Stopping.
• Stop Failed.
• Process State Unknown.

───

15. T11 — Connections & Hosts

Header

• Connections.
• Add Connection.
• Search.

Connection Card

• الاسم.
• نوع الاتصال.
• المضيف.
• الحالة.
• آخر اتصال.
• البيئة المرتبطة.

Connection Details

Overview

بيانات المضيف ونوع الاتصال.

Authentication

طريقة المصادقة، دون عرض الأسرار.

Permissions

الصلاحيات والنطاق.

Health

نتيجة آخر اختبار.

Activity

أحداث الاتصال.

Add Connection Wizard

1. Connection Type.
2. Host Information.
3. Authentication Method.
4. Secure Credential Reference.
5. Permissions.
6. Connection Test.
7. Review.
8. Save.

Security Rules

• الأسرار لا تظهر كنص صريح.
• لا تحفظ في History.
• لا تظهر في Logs.
• لا ترسل إلى AI افتراضيًا.
• يستخدم Secrets Vault لحفظها وفق صلاحياته.
• إلغاء التفويض لا يحذف السجل التدقيقي.

Connection States

• Not Configured.
• Connecting.
• Connected.
• Authentication Failed.
• Unreachable.
• Expired.
• Revoked.
• Unknown.

───

16. T12 — AI Terminal Assistant

نوع الصفحة

لوحة سياقية داخل Terminal، وليست Composer جديدًا.

Header

• AI Assistant.
• سياق الجلسة.
• زر إغلاق اللوحة.
• Context Controls.

Context Summary

يعرض للمستخدم ما يمكن للمساعد الاطلاع عليه:

• Session.
• Environment.
• Shell.
• Working Directory.
• Recent Commands.
• Selected Output.
• Selected Files.
• Related Task عند وجوده.

يمكن للمستخدم إزالة أي مصدر سياق غير مطلوب.

Modes

Explain

شرح أمر أو مخرج.

Suggest

اقتراح أمر.

Diagnose

تشخيص خطأ.

Plan

تحويل الهدف إلى خطوات.

Prepare

تجهيز الأوامر دون تشغيلها.

Execute

إرسال طلب تنفيذ بعد فحص الصلاحيات والموافقة اللازمة.

Response Layout

كل رد يتكون عند الحاجة من:

1. Summary.
2. Proposed Action.
3. Command Preview.
4. Explanation.
5. Expected Impact.
6. Risk.
7. Required Permission.
8. Execution Controls.

Actions

• Copy Command.
• Edit.
• Prepare.
• Ask Follow-up.
• Execute After Approval.
• Save as Snippet.

حالات المساعد

• Idle.
• Resolving Context.
• Thinking.
• Preparing Command.
• Awaiting Approval.
• Executing.
• Completed.
• Failed.
• Context Unavailable.

لا يعرض المساعد الأمر بوصفه منفذًا إلا بعد تأكيد التنفيذ من الجهة المسؤولة.

───

17. T13 — Agents & Execution Runs

الهدف

عرض ومتابعة تنفيذ الوكلاء المرتبط بـ Terminal.

هذه الصفحة لا تنشئ Agent Runtime موازيًا.

Header

• Execution Runs.
• Search.
• Filters.

Run List

• Task Name.
• Agent.
• Environment.
• Session.
• Current Step.
• Run State.
• Started At.
• Approval Status.

Run Details

Overview

الهدف والحالة والوكيل والبيئة.

Plan

الخطة المرتبطة بالمهمة.

Steps

الخطوات المنفذة والمتبقية.

Commands

الأوامر التي نفذها الوكيل بالفعل.

Output

المخرجات.

Approvals

طلبات الموافقة وروابطها إلى Approval Service.

Provenance

النموذج والمزود والدور والمساهمة والتحويلات.

Review

النتائج التي تحتاج مراجعة.

Controls

• Open Session.
• Inspect.
• Review.
• Approve Next Action عبر Approval Service.
• Reject عبر Approval Service.
• Request Stop وفق عقد المهمة أو الوكيل.

لا يظهر Pause أو Resume إلا إذا كان عقد التنفيذ يدعم ذلك.

عرض التنافس

عند وجود عدة وكلاء:

• يعرض المورد المشترك.
• الوكيل الذي يمتلك Claim.
• حالة Lease.
• الوكلاء المنتظرون.
• وجود تعارض.

لا يتخذ Terminal قرارات التنسيق بنفسه؛ يستخدم Orchestration.

───

18. T14 — Permissions & Security

الهدف

عرض الوضع الأمني المرتبط بعمليات Terminal.

هذه الصفحة لا تصبح مالك الصلاحيات أو الموافقات.

Sections

Current Permission Profile

ملف الصلاحيات الحالي.

Environment Scope

البيئات التي يمكن الوصول إليها.

Filesystem Scope

المسارات المصرح بها.

Network Scope

حدود الوصول الشبكي.

Process Scope

حدود إدارة العمليات.

Secrets Access

مراجع التفويض إلى Secrets Vault دون عرض الأسرار.

Recent Security Events

أحداث الأمان المرتبطة بـ Terminal.

Pending Approvals

ملخص طلبات الموافقة مع الانتقال إلى Approval Center العام.

Approval Preview

عند فتح طلب:

• مقدم الطلب.
• الأمر.
• البيئة.
• المسار.
• الأثر.
• المخاطر.
• البيانات الخارجية.
• الصلاحيات المطلوبة.
• قابلية التراجع.

الإجراءات:

• Review Details.
• Open Approval Center.

الموافقة الفعلية تتم في الخدمة العامة.

الحالات

• No Pending Approvals.
• Restricted.
• Permission Expired.
• Revoked.
• Security Warning.
• Policy Unavailable.

───

19. T15 — Logs & Diagnostics

Header

• Logs & Diagnostics.
• Search.
• Filters.
• Export عند السماح.

Filters

• Session Events.
• Command Events.
• Process Events.
• Environment Events.
• Connection Events.
• AI Events.
• Security Events.

Log Row

• Time.
• Event Type.
• Source.
• Session.
• Environment.
• Status.
• Short Message.

Log Details

• Event ID.
• Timestamp.
• Correlation ID عند توفره.
• Related Command.
• Related Process.
• Related Session.
• Related Environment.
• Error Details.
• Provenance عند الحاجة.

AI Diagnostics

يمكن للمستخدم طلب:

• Explain Error.
• Analyze Failure.
• Compare Attempts.
• Suggest Repair.

يعرض AI ملخصًا منفصلًا عن السجل الخام.

Export

قبل التصدير:

• تحديد نطاق السجلات.
• مراجعة البيانات الحساسة.
• توضيح ما سيتم تضمينه.
• احترام سياسة الاحتفاظ والتصدير.

حالات الصفحة

• No Logs.
• Logs Loading.
• Partial Logs.
• Log Source Unavailable.
• Sensitive Data Redacted.

───

20. T16 — Terminal Settings

بنية الصفحة

General

• Default Environment.
• Default Shell.
• Default Working Directory.
• Startup Behavior.

Appearance

• Font Family.
• Terminal Font Size.
• Line Height.
• Cursor Style.
• Cursor Blink.
• Color Scheme.
• Contrast.

Behavior

• Scrollback.
• Word Wrap.
• Auto Scroll.
• Copy Behavior.
• Paste Confirmation.
• Input History.

Sessions

• Default Session Setup.
• Restore Last Sessions.
• Session Naming.
• Session Retention.

Environments

• Preferred Environment.
• Default Execution Mode.
• Resource Preferences.

AI

• Assistant Availability.
• Context Sharing.
• Explain/Suggest/Prepare Defaults.
• Model Selection Policy من النظام العام.

Autonomy

• Default Permission Profile.
• Approval Preferences.
• Execution Limits.
• Stop Conditions.

Keyboard

• Shortcut Mapping.
• Command History.
• Search Output.
• Split View.
• Command Palette.

History & Privacy

• History Retention.
• Log Retention.
• Export Preferences.
• Secret Redaction.

Advanced

• Encoding.
• Shell Integration.
• Debug Options عند الدعم.

Reset

• Reset Setting.
• Reset Category.
• Reset All Terminal Settings.

التفاعل

• كل تعديل يظهر أثره في Preview عند الحاجة.
• الحفظ تلقائي.
• استعادة الافتراضي لا تحذف بيانات المستخدم.
• لا تعرض ميزة غير مدعومة كإعداد قابل للتفعيل.

───

21. T17 — Command Palette

نوع العرض

Overlay سريع فوق الصفحة الحالية.

Header

حقل بحث واحد.

Sections

• Recent Actions.
• Sessions.
• Navigation.
• Commands.
• Environments.
• Processes.
• AI Actions.
• Settings.

Result Row

• اسم الإجراء.
• وصف مختصر.
• اختصار لوحة المفاتيح.
• الحالة: Available / Disabled.

أمثلة

• New Session.
• Switch Session.
• Open Environment.
• Search History.
• Split Terminal.
• Explain Last Error.
• Open Processes.
• Open Settings.

سلوك التنفيذ

• الإجراءات التنقلية تنفذ عند الاختيار.
• الإجراءات الحساسة تفتح مراجعة مستقلة.
• لا ينفذ Command Palette أمر Shell مباشرة بمجرد كتابة اسمه في البحث.

الهاتف

يفتح كطبقة واسعة أو Bottom Sheet قابلة للتمدد.

Desktop

Overlay مركزي يدعم البحث بلوحة المفاتيح.

───

22. التدفقات الرئيسية بين الصفحات

Flow A — أول استخدام

Terminal Home
→ Add Environment
→ Environment Test
→ Review
→ Create
→ New Session
→ Terminal Workspace.

Flow B — تنفيذ أمر يدوي

Terminal Workspace
→ Command Input
→ Validation
→ Permission Check
→ Approval عند الحاجة
→ Execution
→ Output
→ Command History.

Flow C — طلب مساعدة AI

Terminal Workspace
→ Select Error أو Command
→ AI Assistant
→ Context Review
→ Explanation / Suggestion
→ Command Preview
→ User Approval عند الحاجة
→ Execution
→ Result.

Flow D — تنفيذ مهمة بواسطة Agent

Task أو Agent Runtime
→ Execution Request
→ Permission Check
→ Terminal Capability
→ Session / Environment
→ Command Execution
→ Output
→ Agent Review
→ Final Result.

Flow E — تشخيص مشكلة

Terminal Workspace
→ Error
→ Logs & Diagnostics
→ AI Diagnosis
→ Proposed Repair
→ Review
→ Approval
→ Execution
→ Verify Result.

Flow F — الانتقال إلى Forge

Terminal Workspace
→ Open Related Project in Forge
→ Forge Project Context
→ Code Workspace أو صفحة المشروع.

الانتقال لا ينقل ملكية الجلسة أو التنفيذ إلى Forge.

───

23. مصفوفة Responsive

الصفحة| Android Phone| Tablet| Desktop
T01 Home| بطاقات رأسية| أعمدة| Dashboard
T02 Sessions| قائمة بطاقات| قائمة + تفاصيل| جدول + تفاصيل
T03 Workspace| طرفية + أدوات سياقية| Split Panels| Multi-Pane
T04 Session Details| أقسام مطوية| Tabs| Tabs + Sidebar
T05 History| قائمة بحث| قائمة + تفاصيل| جدول
T06 Snippets| بطاقات| Grid| Library Workspace
T07 Environments| بطاقات| Grid| Grid/Table
T08 Environment Details| أقسام| Tabs| Tabs + Panels
T09 File Explorer| قائمة| Explorer مبسط| Explorer + Preview
T10 Processes| بطاقات| جدول مبسط| Data Table
T11 Connections| بطاقات| قائمة + تفاصيل| Table + Details
T12 AI Assistant| Bottom Sheet/Panel| Side Panel| Side Panel
T13 Execution Runs| قائمة| قائمة + تفاصيل| Timeline + Details
T14 Security| أقسام| Panels| Dashboard
T15 Diagnostics| قائمة| قائمة + تفاصيل| Log Explorer
T16 Settings| أقسام| قائمة + تفاصيل| Settings Sidebar
T17 Palette| Bottom Sheet| Overlay| Centered Overlay

24. التفاعل مع Application Shell

1. Terminal لا يضيف Bottom Navigation عامة.
2. Terminal لا ينشئ Global Composer جديدًا.
3. Composer العام يستقبل سياق Terminal عند اختيار المستخدم.
4. التنقل بين المساحات يحافظ على Drafts.
5. الرجوع لا يلغي عملية أو مهمة.
6. إغلاق لوحة AI لا يوقف التنفيذ.
7. تغيير Theme لا يعيد تعيين الجلسة.
8. استعادة التطبيق تتحقق من الحالة الفعلية للجلسات والعمليات.
9. لا يعاد استخدام صلاحية منتهية بعد الاستعادة.
10. إشعارات Terminal تستخدم نظام Notifications العام.

25. Model Provenance داخل الصفحات

تظهر التفاصيل المختصرة افتراضيًا، مع إمكانية فتح تفاصيل المصدر.

يجب أن تدعم الصفحات ذات الصلة عرض:

• Model.
• Version.
• Provider.
• Execution Location.
• Role.
• Contribution.
• Start/Completion Time.
• Status.
• Participating Models.
• Handoff.
• Related Agent/Task.

في History وExecution Runs وCommand Details، يجب أن يستطيع المستخدم التمييز بين:

• أمر كتبه بنفسه.
• أمر اقترحه AI ولم ينفذ.
• أمر أرسله AI وتم تنفيذه.
• أمر نفذه Agent.
• أمر جاء من Workflow.

لا تنسب مخرجات المستخدم إلى نموذج.

26. قواعد العرض للمبتدئ والمحترف

Simple View

• شرح مبسط.
• أزرار واضحة.
• إخفاء التفاصيل التقنية غير الضرورية.
• معاينة الأوامر.
• توضيح الأثر قبل التنفيذ.
• إبراز الأخطاء بلغة مفهومة.

Standard View

• معلومات البيئة والجلسة.
• الأوامر والمخرجات.
• حالات التنفيذ.
• إجراءات أكثر تفصيلًا.

Advanced View

• Logs.
• Raw Output.
• Exit Codes.
• Process IDs.
• Runtime Details.
• Execution Metadata.
• Keyboard Shortcuts.
• Environment Capabilities.

تغيير مستوى العرض لا يغير الصلاحيات أو مستوى الحماية.

27. حالات حرجة يجب اختبارها في التصميم

• انقطاع الاتصال أثناء تنفيذ أمر.
• فقد الاتصال بعد إرسال أمر وقبل وصول النتيجة.
• إعادة فتح التطبيق أثناء تشغيل عملية.
• انتهاء صلاحية اتصال.
• انتهاء صلاحية Permission.
• محاولة Agent تنفيذ أمر خارج النطاق.
• محاولة تنفيذ أمر على بيئة غير متاحة.
• مخرجات طويلة جدًا.
• مخرجات تحتوي نصوصًا عربية وإنجليزية مختلطة.
• نص يحتوي مسارات وURLs ورموزًا خاصة.
• إغلاق Session مع وجود Process.
• تغيير البيئة أثناء وجود جلسة.
• إيقاف Agent بينما Tool Invocation لا يزال نشطًا.
• طلب موافقة أثناء وجود أكثر من عملية.
• محاولة إعادة تنفيذ أمر ذي أثر خارجي بعد نتيجة غير مؤكدة.
• استعادة Session مع اختلاف حالة Runtime عن آخر حالة محفوظة.

28. معايير قبول التصميم التفصيلي

الصفحات

[  ]  الصفحات T01–T17 محددة.
[  ]  كل صفحة لها هدف ومحتوى وإجراءات.
[  ]  كل صفحة لها Empty وLoading وError States.
[  ]  التنقل متوافق مع Shell.
[  ]  لا توجد خدمات مكررة.

Workspace

[  ]  الطرفية هي المساحة الأساسية.
[  ]  الإدخال والمخرجات واضحان.
[  ]  الأوامر والمخرجات الأصلية منفصلة عن تعليقات AI.
[  ]  الجلسات المتعددة مفهومة.
[  ]  Split View لا يفرض على الهاتف.
[  ]  العمليات الخلفية لا تتوقف بسبب التنقل.

AI & Agents

[  ]  AI Assistant سياقي وليس Composer مستقلًا.
[  ]  مصادر السياق مرئية.
[  ]  الأمر المقترح منفصل عن الأمر المنفذ.
[  ]  كل تنفيذ له حالة وأدلة.
[  ]  Agent Runtime يظل مالك حالة الوكيل.
[  ]  Orchestration يظل مالك التنسيق.

Security

[  ]  Approval Center عام.
[  ]  Secrets Vault عام.
[  ]  الصلاحيات محددة النطاق.
[  ]  لا تسريب للأسرار في History أو Logs.
[  ]  الإجراءات الحساسة تحتاج الموافقة المناسبة.

Responsive

[  ]  Android Phone.
[  ]  Tablet.
[  ]  Desktop/Web.
[  ]  RTL/LTR.
[  ]  Keyboard & Safe Areas.
[  ]  Touch Targets.
[  ]  Accessibility.

29. العلاقة بالمواصفة الشاملة

هذه الوثيقة تفصل تصميم الصفحات ولا تستبدل:

• الرؤية والحدود المعمارية.
• Capability Manifest.
• Execution Contracts.
• Session Contracts.
• Environment Contracts.
• Permission Contracts.
• Provenance Contract.
• Error and Recovery Semantics.

تظل هذه الموضوعات مرجعية في الوثيقة الشاملة والوثائق المعمارية والعقود المخصصة.

30. حالة الوثيقة

• Screen Inventory: Draft Complete.
• Page Layouts: Draft Complete.
• Interaction Definitions: Draft Complete.
• Responsive Behavior: Draft Complete.
• Architecture Reconciliation: Reviewed Against Supplied Documentation Package.
• Registry Approval: Pending.
• Visual Mockups: Not Created.
• UI Implementation: Not Verified.
• Runtime Implementation: Not Verified.
• Device Testing: Pending.
• Accessibility Testing: Pending.

هذه مواصفة تصميم تفصيلية وليست دليلًا على تنفيذ الشاشات أو تشغيل القدرات.
