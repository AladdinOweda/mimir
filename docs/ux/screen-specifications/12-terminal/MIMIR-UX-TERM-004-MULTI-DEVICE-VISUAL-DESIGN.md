Mimir Terminal — Complete Multi-Device Screen Design Specification

Document Status: Comprehensive Visual Design Draft — Pending Review
Related Documents:

• Mimir Terminal — Screen-by-Screen UX & Interaction Specification
• Mimir Terminal — Navigation & User Journey Map
• Mimir Design Tokens & Theme Contract
• Mimir Component Library Specification
• Mimir Application Shell Specification

Design Order: Android → Tablet → Desktop
Screen Families: 17
Design Scope: All 17 screens across all three device categories
Implementation Status: Not implemented or runtime-tested

───

1. التصميم العام لـ Mimir Terminal

1.1 الشخصية البصرية

Terminal يجب أن يبدو كمساحة عمل احترافية حقيقية، وليس صفحة Chat تحتوي على صندوق أسود.

الشخصية المطلوبة:

• هادئة واحترافية.
• عملية ومباشرة.
• كثافة المعلومات قابلة للتحكم.
• Terminal Output هو مركز الاهتمام.
• AI Assistant مساعد وليس العنصر المسيطر.
• الأدوات المتقدمة متاحة عند الحاجة.
• الهوية البصرية متوافقة مع Mimir Liquid Design.

1.2 نظام الألوان والمظهر

نستخدم نظام Mimir المعتمد:

• Pearl Light.
• Hybrid Dark / Light.
• Balanced Liquid Design.
• Adaptive Motion.
• درجات الظلال الثلاث المستقلة.
• Glass وTransparency بشكل متزن.

قاعدة Terminal المهمة: منطقة الأوامر والمخرجات تحتاج خلفية ثابتة عالية التباين، حتى في وضع Light. لا نضع تأثيرات Glass أو Blur قوية خلف النصوص التقنية.

1.3 نظام الخطوط

هناك فصل واضح بين:

نوع المحتوى| الخط
واجهة Mimir| Global UI Font
Terminal Commands| Monospace
Terminal Output| Monospace
AI Explanations| Global UI Font
Code / Paths| Monospace

Zoom داخل Terminal مستقل عن حجم خط Mimir العام.

1.4 الهيكل العام

تستخدم الشاشات جميعها:

• Global Shell Navigation.
• Terminal Header.
• Contextual Navigation.
• Main Content.
• Contextual Panels عند الحاجة.
• Global Composer من Shell فقط.
• Global Bottom Navigation على الهاتف.

لا نكرر Composer داخل Terminal.

───

2. قواعد التصميم حسب الجهاز

Android

الفلسفة: شاشة واحدة واضحة في كل مرة، مع أدوات سياقية قابلة للفتح والإغلاق.

• مساحة المحتوى الرئيسية تملأ الشاشة.
• التنقل الداخلي لا يزاحم Terminal Output.
• التفاصيل تفتح كصفحات أو لوحات مناسبة.
• القوائم الثانوية تستخدم Bottom Sheets عند ملاءمتها.
• لوحة المفاتيح لا تغطي منطقة العمل.
• الأوامر الطويلة تظل قابلة للتمرير والنسخ.
• لا توجد أعمدة جانبية دائمة ضيقة.

Tablet

الفلسفة: مساحة عمل أوسع تسمح بتقسيم الواجهة.

• قائمة جلسات أو أدوات على الجانب.
• Workspace في المنتصف.
• لوحة سياقية اختيارية.
• يمكن عرض AI أو Processes أو Logs بجانب Workspace.
• اللوحات قابلة للتوسيع والإخفاء.
• اللمس ولوحة المفاتيح الخارجية مدعومان تصميميًا.

Desktop

الفلسفة: بيئة هندسية احترافية متعددة اللوحات.

• Navigation Panel.
• Workspace رئيسي.
• Context Panel اختياري.
• Multi-session navigation.
• Resizable Panels.
• Command Palette.
• Keyboard Shortcuts.
• إمكانية توزيع Logs وProcesses وAI دون تقليص Terminal Output بشكل مبالغ فيه.

───

3. تصميم الشاشات الـ17

T01 — Terminal Home

الهدف

مركز الانطلاق إلى جلسات Terminal والأدوات المهمة.

Android

الترتيب:

1. Terminal Header.
2. زر New Session.
3. Continue Session عند وجود جلسة سابقة.
4. Active Sessions.
5. Recent Activity.
6. Quick Access.
7. حالة التنبيهات أو المشكلات عند وجودها.

الأولوية البصرية لبدء جلسة أو استكمالها، وليس لعرض الإحصائيات.

Tablet

• Navigation مختصر على الجانب.
• Active Sessions في مساحة رئيسية.
• Quick Access بجانب الجلسات.
• Recent Activity في قسم سفلي أو لوحة قابلة للفتح.

Desktop

• Navigation Panel ثابت.
• Active Sessions كمنطقة رئيسية.
• Recent Activity بجانبها أو أسفلها.
• Quick Actions في منطقة مختصرة أعلى المحتوى.

الحالة الفارغة: رسالة بسيطة تشرح وظيفة Terminal وزر بدء أول جلسة.

T02 — Sessions

الهدف

إدارة الجلسات الحالية والسابقة.

Android

• قائمة جلسات على شكل Cards مضغوطة.
• اسم الجلسة.
• البيئة.
• حالة الجلسة.
• آخر نشاط.
• قائمة إجراءات سياقية.
• البحث والتصفية في الجزء العلوي.

فتح الجلسة ينقل إلى Workspace، وفتح تفاصيلها ينقل إلى Session Details.

Tablet

• قائمة جلسات في الجانب.
• تفاصيل مختصرة للجلسة المختارة في المساحة الرئيسية.
• فتح Workspace من نفس السياق.

Desktop

• Sessions Panel دائم.
• Session Tabs أو Session Switcher.
• قائمة جلسات قابلة للبحث.
• معاينة تفاصيل الجلسة في Panel مستقل.

الحالات: Active, Idle, Disconnected, Interrupted, Completed, Unavailable.

T03 — Terminal Workspace

الهدف

المساحة الرئيسية لإدخال الأوامر وتنفيذها ومتابعة المخرجات.

Android

التصميم:

• Header صغير باسم الجلسة والبيئة.
• مساحة Terminal Output كبيرة.
• Command Input واضح ومخصص لـ CLI.
• مؤشر حالة التنفيذ.
• شريط أدوات سياقي: AI / Processes / Files / History / Details.
• إمكانية إخفاء لوحة الأدوات لتوسيع مساحة المخرجات.

لوحة المفاتيح يجب أن تفتح دون تغطية منطقة الإدخال أو آخر مخرجات مهمة.

التمييز البصري:

• Command.
• Output.
• Error.
• Warning.
• System Message.
• AI Suggestion.

Tablet

• Terminal Output في المنتصف.
• Session Navigation على الجانب.
• Context Panel اختياري للـ AI أو Processes.
• إمكانية تغيير عرض اللوحات.
• دعم لوحة مفاتيح خارجية.

Desktop

• Terminal Workspace مركزي.
• جلسات متعددة عبر Tabs أو Session Switcher.
• Context Panel على الجانب.
• إمكانية تقسيم مساحة Terminal إلى أكثر من View عند دعم ذلك.
• Command Palette واختصارات لوحة المفاتيح.
• Scroll مستقل للمخرجات.

قاعدة مهمة: مخرجات AI لا تختلط بصريًا مع المخرجات الفعلية للأوامر.

T04 — Session Details

الهدف

عرض بيانات الجلسة وحالتها وإعداداتها.

Android

• صفحة تفاصيل منفصلة.
• Session Identity.
• Environment.
• Connection State.
• Created / Last Activity.
• Associated Processes.
• إجراءات الجلسة المسموح بها.

Tablet

• Session Details Panel بجانب قائمة الجلسات.
• يمكن الانتقال إلى Workspace دون فقدان السياق.

Desktop

• Details Panel أو Tab.
• معلومات منظمة في أقسام واضحة.
• Session Events عند الحاجة.

لا يعرض التصميم بيانات غير متاحة في البيئة الفعلية.

T05 — Command History

الهدف

استعراض الأوامر السابقة والبحث فيها وإعادة استخدامها.

Android

• Search Bar.
• Filters.
• قائمة أوامر مرتبة زمنيًا.
• الأمر.
• الجلسة المرتبطة.
• الوقت.
• نتيجة التنفيذ عند توفرها.
• زر Insert into Workspace.

Tablet

• قائمة في الجانب.
• تفاصيل الأمر المختار في المساحة الرئيسية.
• عرض الأمر كاملًا دون ضغط النص.

Desktop

• History Table أو List.
• أعمدة للحالة والوقت والجلسة والبيئة.
• بحث وتصفية.
• Details Panel.
• إدراج الأمر في Workspace.

حماية الخصوصية: لا تظهر القيم السرية في History.

T06 — Commands & Snippets

الهدف

مكتبة الأوامر والقوالب القابلة لإعادة الاستخدام.

Android

• تصنيفات مختصرة.
• بحث.
• Snippet Cards.
• معاينة الأمر.
• Copy / Insert.
• إمكانية إنشاء أو تعديل Snippet إذا كانت الإمكانية مدعومة.

Tablet

• قائمة التصنيفات على الجانب.
• تفاصيل Snippet في المنتصف.
• معاينة الأمر في Panel إضافي.

Desktop

• مكتبة متعددة الأعمدة أو قائمة تفصيلية.
• محرر Snippet عند الحاجة.
• Metadata.
• Tags.
• Preview.
• Insert into Workspace.

إدراج Snippet لا ينفذ الأمر تلقائيًا.

T07 — Environments

الهدف

عرض بيئات التنفيذ المتاحة.

Android

• Environment Cards.
• اسم البيئة.
• نوعها.
• حالة التوفر.
• معلومات مختصرة عن قدراتها.
• زر View Details.

Tablet

• قائمة بيئات على الجانب.
• تفاصيل مختصرة للبيئة المختارة.
• جلسات مرتبطة بها.

Desktop

• Environment List.
• Environment Status.
• Capabilities.
• Associated Sessions.
• Search and Filters.

يجب إظهار Local / Remote / Cloud فقط عندما تكون طبيعة البيئة معروفة ومدعومة.

T08 — Environment Details

الهدف

عرض تفاصيل البيئة وقدراتها وإعداداتها.

Android

أقسام مرتبة:

1. Overview.
2. Runtime Information.
3. Capabilities.
4. Associated Sessions.
5. Configuration.
6. Diagnostics.

Tablet

• قائمة الأقسام على الجانب.
• تفاصيل القسم في المساحة الرئيسية.

Desktop

• Overview Panel.
• Configuration Panel.
• Capabilities Panel.
• Sessions and Diagnostics ضمن Tabs أو Sections.

أي تغيير في البيئة يجب أن يظهر أثره قبل اعتماده إذا كان مؤثرًا.

T09 — File & Path Explorer

الهدف

استعراض المسارات والملفات المرتبطة ببيئة Terminal.

Android

• Path Header.
• Breadcrumbs.
• قائمة ملفات ومجلدات.
• نوع العنصر وحجمه عند توفرهما.
• قائمة إجراءات سياقية.
• نسخ المسار.
• Open in Mimir Files أو Forge حسب الحاجة.

Tablet

• File Tree على الجانب.
• محتوى المسار في الوسط.
• تفاصيل العنصر في Panel اختياري.

Desktop

• File Tree.
• File List.
• Details Panel.
• Breadcrumb Navigation.
• Context Menu.

لا نعيد بناء Mimir Files داخل Terminal. هذه الشاشة مستكشف سياقي للمسارات فقط.

T10 — Processes & Jobs

الهدف

متابعة العمليات والمهام المرتبطة ببيئة Terminal.

Android

• Process Cards.
• Process Name / ID.
• الحالة.
• المدة.
• الجلسة المرتبطة.
• فتح التفاصيل.
• إجراء Stop عند دعمه والتصريح به.

Tablet

• قائمة العمليات على الجانب.
• Process Details في المنتصف.
• Logs في Panel إضافي.

Desktop

• Process Table.
• Filters.
• Process Details.
• Logs and Events.
• إجراءات مسموحة بوضوح.

يجب عدم الخلط بين Process وSession وAgent Run.

T11 — Connections & Hosts

الهدف

إدارة الاتصالات والوجهات التي يمكن أن تستخدمها البيئات.

Android

• قائمة الاتصالات.
• الاسم والنوع.
• الحالة.
• آخر فحص.
• Add Connection.
• Test Connection.

تُعرض أنواع الاتصال المتاحة فعليًا فقط.

Tablet

• Connections List.
• Connection Details.
• Configuration أو Test Results في لوحة منفصلة.

Desktop

• Connection Navigation.
• Details Panel.
• Configuration Form.
• Test Results.
• Associated Environments.

بيانات الاعتماد تستخدم Secure Vault ولا تظهر كنص عادي.

T12 — AI Terminal Assistant

الهدف

مساعدة المستخدم على فهم الأوامر والأخطاء وإعداد الحلول.

Android

• لوحة Assistant سياقية.
• Context Summary.
• Explain.
• Diagnose.
• Suggest.
• Prepare Command.
• Request Execution.

الاقتراحات تظهر كبطاقات منفصلة عن Terminal Output.

Tablet

• Assistant Side Panel.
• Terminal Workspace يبقى ظاهرًا بجانبه.
• إمكانية مقارنة الاقتراح بالمخرجات.

Desktop

• Assistant Panel قابل لتغيير الحجم.
• Conversation Context.
• Suggested Commands.
• Evidence / Sources عند توفرها.
• Insert into Workspace.

لا ينفذ Assistant الأمر لمجرد عرضه أو اقتراحه.

T13 — Agents & Execution Runs

الهدف

متابعة تشغيل Agents والعمليات التي ينفذونها.

Android

• Runs List.
• Agent Identity.
• Model / Provider عند توفرهما.
• حالة التنفيذ.
• Progress.
• Details.
• Logs.
• Evidence.

Tablet

• Runs List في الجانب.
• Run Details في المنتصف.
• Timeline أو Logs في Panel إضافي.

Desktop

• Runs Navigation.
• Execution Timeline.
• Steps.
• Tool Invocations.
• Output.
• Errors.
• Verification Evidence.

يجب توضيح مساهمة النموذج ونوع التنفيذ Local / Cloud / Hybrid عند توفر المعلومات.

T14 — Permissions & Security

الهدف

عرض صلاحيات Terminal والسياسات المرتبطة به.

Android

• Permission Overview.
• Current Environment Permissions.
• Granted / Denied / Required.
• Security Warnings.
• رابط إلى Global Approval Center عند وجود طلب موافقة.

Tablet

• قائمة الصلاحيات.
• تفاصيل الصلاحية المختارة.
• نطاق التأثير.

Desktop

• Permissions Overview.
• Policy Details.
• Access Scope.
• Recent Security Events.
• روابط الخدمات المشتركة.

هذه الصفحة ليست Approval Center مستقلًا.

T15 — Logs & Diagnostics

الهدف

تشخيص المشكلات ومراجعة السجلات.

Android

• اختيار Session أو Process أو Environment.
• قائمة الأحداث.
• Filters.
• Error Details.
• Copy Diagnostics عند السماح بذلك.
• AI Explanation.

Tablet

• Logs List على الجانب.
• Details في الوسط.
• Filters في أعلى اللوحة.

Desktop

• Logs Viewer واسع.
• Filters متقدمة.
• Search.
• Event Details.
• Correlation مع Session وProcess وAgent Run.

يجب إخفاء البيانات السرية من السجلات.

T16 — Terminal Settings

الهدف

تخصيص تجربة Terminal دون تكرار إعدادات Mimir العامة.

Android

تقسيم الإعدادات إلى:

• Terminal Appearance.
• Terminal Font & Zoom.
• Command Behavior.
• Session Preferences.
• Keyboard & Shortcuts.
• Notifications.
• Advanced Options.

الإعدادات العامة مثل Global Font وTheme تُدار من إعدادات Mimir الأصلية.

Tablet

• Settings Navigation على الجانب.
• إعدادات القسم المختار في المساحة الرئيسية.
• معاينة مباشرة عند الحاجة.

Desktop

• Settings Sidebar.
• Settings Content.
• Live Preview.
• Advanced Options ضمن أقسام قابلة للفتح.

يجب توفير Restore Defaults.

T17 — Command Palette

الهدف

الوصول السريع إلى الصفحات والإجراءات والأوامر المتاحة.

Android

• Overlay مناسب للموبايل.
• Search Input.
• نتائج مصنفة.
• صفحات Terminal.
• Sessions.
• Commands.
• أدوات سياقية.
• اختصارات الإجراءات.

Tablet

• نافذة وسطية أو Overlay واسع.
• نتائج في قائمة.
• تفاصيل مختصرة عند التحديد.

Desktop

• Palette في مركز الشاشة.
• اختصار لوحة مفاتيح.
• Search سريع.
• Recent Actions.
• Context-aware Results.

يجب ألا تعرض إجراءات غير مسموحة للمستخدم أو غير مدعومة في البيئة.

───

4. مكونات مشتركة عبر الشاشات

4.1 Terminal Header

يعرض:

• اسم الصفحة أو الجلسة.
• البيئة الحالية عند وجودها.
• حالة الاتصال.
• قائمة إجراءات سياقية.

4.2 Status Indicators

حالات موحدة:

• Ready.
• Running.
• Awaiting Approval.
• Succeeded.
• Failed.
• Interrupted.
• Disconnected.
• Unknown.

يجب أن تستخدم الحالة نصًا ورمزًا واضحين، وليس اللون وحده.

4.3 Contextual Panels

تستخدم لـ:

• AI Assistant.
• Process Details.
• Session Details.
• Logs.
• File Details.

لا تُعرض كلها في وقت واحد على الهاتف.

4.4 Command Preview

قبل التنفيذ عند الحاجة:

• الأمر الكامل.
• البيئة المستهدفة.
• الأثر المتوقع.
• الصلاحيات.
• الحاجة إلى موافقة.
• إمكانية التراجع إذا كانت معروفة.

4.5 Provenance

كل مخرجات AI أو Agent تحتفظ ببيانات المصدر المتاحة:

• Model.
• Provider.
• Execution Type.
• Role / Contribution.
• Timestamp.
• Status.
• Tool Invocations.
• Verification Evidence.

العرض الافتراضي مختصر، والتفاصيل تظهر عند الطلب.

───

5. تصميم التفاعل بين الأجهزة

الوظيفة| Android| Tablet| Desktop
Sessions| صفحة مستقلة| Side Panel| Persistent Panel
Terminal Output| شاشة أساسية| Main Area| Main Workspace
AI Assistant| Contextual Sheet| Side Panel| Resizable Panel
Processes| صفحة| Panel| Panel / Table
Logs| صفحة| Expandable Panel| Dedicated Viewer
File Explorer| صفحة| Split View| Multi-pane
History| صفحة| List + Details| Table + Details
Command Palette| Overlay| Overlay| Keyboard-first Overlay
Settings| صفحة| Navigation + Content| Sidebar + Content

───

6. التصميم في الوضعين Dark وLight

Dark

• خلفيات عميقة ومتزنة.
• منطقة Terminal عالية التباين.
• تمييز دقيق بين Panels.
• ألوان حالة واضحة.
• تجنب الوهج والسطوع المفرط.

Light

• خلفيات Pearl.
• منطقة Terminal مستقلة بتباين قوي.
• النصوص التقنية واضحة.
• الحدود والظلال خفيفة.
• عدم استخدام درجات رمادية منخفضة التباين للنصوص الأساسية.

يجب أن يظل توزيع العناصر متطابقًا في الوضعين، دون تغيير معنى الألوان أو الحالات.

───

7. الأولويات البصرية للتنفيذ التصميمي

تُعطى الأولوية لهذه الشاشات عند إنشاء النماذج المرئية:

1. T03 Terminal Workspace.
2. T01 Terminal Home.
3. T02 Sessions.
4. T12 AI Terminal Assistant.
5. T10 Processes & Jobs.
6. T15 Logs & Diagnostics.
7. بقية الشاشات وفق الترتيب.

السبب: هذه الشاشات تحدد معظم أنماط Terminal ومكوناته المشتركة.

───

8. مراجعة التصميم قبل اعتماده

لكل شاشة يجب مراجعة:

• وضوح الهدف.
• ترتيب المعلومات.
• سهولة الوصول للإجراء الأساسي.
• عدم ازدحام الواجهة.
• وضوح حالة التنفيذ.
• سهولة الرجوع.
• عدم فقدان السياق.
• التوافق مع RTL/LTR.
• القراءة في Dark وLight.
• سهولة الاستخدام باللمس.
• اتساق المكونات بين الأجهزة.
• عدم وجود خصائص غير مدعومة.

───

9. حالة الوثيقة

البند| الحالة
17 Screen Designs — Conceptual Specification| Draft Completed
Android Layouts| Draft Completed
Tablet Layouts| Draft Completed
Desktop Layouts| Draft Completed
Shared Components| Draft Completed
Dark / Light Behavior| Draft Completed
Actual Pixel-Level Mockups| Pending
Visual Comparison Against Implemented UI| Pending
Runtime / Interaction Testing| Not Performed
Official Registry Approval| Pending

ملاحظة اعتماد: هذه الوثيقة تكمل المواصفات السابقة ولا تستبدلها أو تنشئ صفحات إضافية. أي ميزة لا تدعمها البيئة الفعلية تظل غير متاحة حتى يتم التحقق منها.
