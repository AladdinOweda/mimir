Mimir Chat — Complete Engineering Wireframes & Interaction Specification

1. الغرض وحدود المساحة

Chat هي مساحة المحادثة والتفاعل مع نماذج الذكاء الاصطناعي والأدوات والوكلاء. وهي ليست مالكة لتنفيذ Browser أو Terminal أو عمليات البناء في Forge؛ تستدعي هذه القدرات عبر Universal Kernel والعقود المشتركة.

المسؤوليات الأساسية:

• إدارة المحادثات والرسائل.
• اختيار النموذج ومتابعة التنفيذ.
• إدارة سياق المحادثة والمرفقات.
• عرض استخدام الأدوات والوكلاء.
• عرض مصدر كل إجابة ومساهمات النماذج.
• إدارة استمرارية المحادثة واستعادتها.
• ربط المحادثات بالمهام ومساحات العمل الأخرى.

خارج مسؤولية Chat:

• تنفيذ أوامر النظام مباشرة.
• إدارة Browser Runtime.
• إدارة عمليات البناء.
• امتلاك أسرار Vault.
• تجاوز سياسات Kernel أو صلاحيات المستخدم.

───

2. القواعد الهندسية

الخاصية| الهاتف
عرض مرجعي| 360dp
هامش المحتوى| 16dp
ارتفاع Header| 56dp
الحد الأدنى للّمس| 48dp
Composer| ثابت أسفل المحادثة
Message Width| حتى 88% من العرض المتاح
Message Spacing| 12dp
Section Spacing| 16dp

• المحادثة هي المساحة الأساسية.
• Composer مشترك على مستوى Application Shell، ويُستدعى في Chat دون إنشاء Composer مستقل مكرر.
• لا يغطي Composer آخر رسالة أو أزرار المراجعة.
• تفاصيل النموذج والتنفيذ تظهر عند الطلب، بينما يظل العرض الافتراضي بسيطًا.
• كل رسالة ومهمة واستدعاء أداة يحمل معرفًا ثابتًا.

───

3. مخططات الشاشات

Screen 01 — Chat Home

الهدف: الوصول إلى المحادثات والبدء بمحادثة جديدة.

┌────────────────────────────────┐
│ Chat Search │
├────────────────────────────────┤
│ New Conversation │
│ ┌────────────────────────────┐ │
│ │ Ask Mimir anything... │ │
│ └────────────────────────────┘ │
│ │
│ Recent Conversations │
│ ┌────────────────────────────┐ │
│ │ Conversation Title │ │
│ │ Last Message / Time │ │
│ │ Model / Status ⋮ │ │
│ └────────────────────────────┘ │
│ │
│ Pinned │
│ ... │
│ │
│ Recent Activity │
├────────────────────────────────┤
│ Home Projects Tasks More │
└────────────────────────────────┘

المكونات: New Conversation، Recent، Pinned، Search، Activity.

التفاعلات:

• New Conversation يفتح شاشة اختيار النموذج والسياق.
• الضغط على محادثة يستعيد Conversation ID.
• Pin/Unpin لا يغير محتوى المحادثة.
• Search يفتح Conversation Search.
• لا تُنشأ محادثة جديدة عند مجرد فتح الصفحة.

الحالات: Empty، Loading، Offline، Partial Sync، Error.

───

Screen 02 — Conversation Workspace

الهدف: المحادثة الأساسية.

┌────────────────────────────────┐
│ ← Conversation Model ⋮ │
├────────────────────────────────┤
│ │
│ User Message │
│ │
│ Assistant Response │
│ │
│ Tool Activity (if any) │
│ │
│ Assistant Response │
│ │
│ Message Actions │
│ Copy / Retry / Details │
│ │
│ │
├────────────────────────────────┤
│ Context / Attachments │
│ Shared Composer Send │
└────────────────────────────────┘

المواصفات:

• الرسائل بترتيب زمني موحد.
• User وAssistant وTool Results لها أنماط مميزة.
• Tool Activity لا تُعرض كرسالة عادية مجهولة المصدر.
• Streaming يضيف محتوى تدريجيًا إلى الرسالة نفسها.
• Composer ثابت ويستوعب النص والمرفقات.
• عنوان النموذج يعرض النموذج الفعلي المستخدم في الرد.

التفاعلات:

• Send يبدأ Generation Request.
• Stop يرسل Cancellation إلى Model Runtime.
• Retry ينشئ محاولة جديدة مرتبطة بالرسالة الأصلية.
• Edit Message يعرض نسخة قابلة للتحرير ويحافظ على أصل الرسالة وفق سياسة المحادثة.
• Regenerate لا يمحو Attribution السابق.
• Continue يرسل طلبًا لاستكمال الرد.
• Message Details يعرض المصدر والحالة والتوقيت.

───

Screen 03 — New Conversation & Model Selection

┌────────────────────────────────┐
│ ← New Conversation │
├────────────────────────────────┤
│ Choose Provider │
│ Provider List │
│ │
│ Choose Model │
│ Model Name / Capabilities │
│ │
│ Execution │
│ Local / Cloud │
│ │
│ Context │
│ None / Current Project / Custom│
│ │
│ Tools │
│ Available Capabilities │
│ │
│ Advanced Options │
├────────────────────────────────┤
│ Create Conversation │
└────────────────────────────────┘

قواعد:

• Discovery لا يساوي Enablement.
• لا يظهر نموذج غير مفعّل كخيار قابل للتشغيل.
• يعرض Context Window والقدرات ذات الصلة.
• يعرض Local/Cloud بوضوح.
• اختيار الأدوات لا يمنح صلاحيات التنفيذ؛ كل استدعاء يخضع للسياسة.
• لا يُنشأ Conversation ID إلا عند إنشاء المحادثة أو بدء أول رسالة وفق تصميم Runtime.

───

Screen 04 — Conversation Search

┌────────────────────────────────┐
│ ← Search Conversations │
├────────────────────────────────┤
│ Search Input │
│ │
│ Filters │
│ Date / Model / Project │
│ │
│ Results │
│ ┌────────────────────────────┐ │
│ │ Conversation Title │ │
│ │ Matching Message │ │
│ │ Date / Model │ │
│ └────────────────────────────┘ │
│ │
└────────────────────────────────┘

التفاعلات: بحث فوري أو عند الإرسال، فلترة، فتح النتيجة في موضع الرسالة، إظهار النتائج الجزئية عند تعذر فهرسة بعض المحادثات.

───

Screen 05 — Conversation History

┌────────────────────────────────┐
│ ← History Filter │
├────────────────────────────────┤
│ Today │
│ Conversation Item │
│ │
│ Yesterday │
│ Conversation Item │
│ │
│ Earlier │
│ Conversation Item │
│ │
└────────────────────────────────┘

• تجميع زمني.
• بحث وترشيح.
• Archive منفصل عن Delete.
• استعادة المحادثة لا تنشئ نسخة غير مرتبطة.
• الحذف يوضح أثره على الملفات والمرفقات والمراجع المرتبطة.

───

Screen 06 — Conversation Details

┌────────────────────────────────┐
│ ← Conversation Details │
├────────────────────────────────┤
│ Title / Created At │
│ Last Activity │
│ │
│ Model History │
│ Participants / Agents │
│ │
│ Project / Workflow Links │
│ │
│ Context Summary │
│ Attachments │
│ │
│ Export / Rename / Archive │
│ Delete │
└────────────────────────────────┘

• يعرض بيانات المحادثة دون عرض أسرار أو سياق غير مصرح به.
• Rename لا يغير Conversation ID.
• Archive لا يعني Delete.
• Export يحترم صلاحيات الملفات والمحتوى.

───

Screen 07 — Attachments & Files

┌────────────────────────────────┐
│ ← Attachments │
├────────────────────────────────┤
│ Add File / Image / Document │
│ │
│ Attached Files │
│ ┌────────────────────────────┐ │
│ │ File Icon / Name │ │
│ │ Type / Size / Status │ │
│ │ Preview / Remove │ │
│ └────────────────────────────┘ │
│ │
│ Processing Status │
│ Extracted Content / Sources │
└────────────────────────────────┘

التفاعلات:

• اختيار ملف.
• التحقق من النوع والحجم.
• فحص الملف قبل المعالجة.
• عرض حالة استخراج النص.
• إزالة المرفق من سياق الطلب دون حذف الملف الأصلي.
• إظهار مصدر الملف وصلاحياته.
• لا يُرسل محتوى ملف كامل للنموذج دون أن يكون ضمن Context Package المصرح به.

───

Screen 08 — Tool Activity & Execution

┌────────────────────────────────┐
│ ← Tool Activity │
├────────────────────────────────┤
│ Current Conversation │
│ │
│ Tool Call │
│ Tool / Capability │
│ │
│ Input Summary │
│ Permission / Risk │
│ │
│ Execution State │
│ Waiting / Running / Completed │
│ │
│ Result / Evidence │
│ Error Details │
├────────────────────────────────┤
│ Review / Cancel / Open Result │
└────────────────────────────────┘

• كل Tool Call له Operation ID.
• يعرض Capability المطلوبة.
• يعرض الموافقة المطلوبة.
• يوضح ما إذا كان التنفيذ محليًا أو بعيدًا.
• لا يخلط Tool Result مع Model Response.
• يحتفظ بخطأ التنفيذ الحقيقي.

───

Screen 09 — Model Comparison

┌────────────────────────────────┐
│ ← Model Comparison │
├────────────────────────────────┤
│ Shared Prompt / Context │
│ │
│ Model A │
│ Provider / Model │
│ Response / Status │
│ │
│ Model B │
│ Provider / Model │
│ Response / Status │
│ │
│ Compare / Synthesize │
└────────────────────────────────┘

• الهاتف: نتائج متتابعة.
• التابلت: عمودان.
• Desktop: مقارنة متوازية.
• كل نموذج يحتفظ باستقلال نتيجة التنفيذ.
• Synthesis نتيجة جديدة مرتبطة بالمساهمات الأصلية.
• فشل نموذج لا يلغي نتائج النماذج الأخرى.

───

Screen 10 — Agent / Multi-model Conversation

┌────────────────────────────────┐
│ ← Agent Conversation │
├────────────────────────────────┤
│ Task Objective │
│ │
│ Coordinator │
│ │
│ Agent A Agent B │
│ Status Status │
│ Contribution Contribution │
│ │
│ Handover / Shared Findings │
│ │
│ Final Synthesis │
├────────────────────────────────┤
│ Follow-up Composer │
└────────────────────────────────┘

• يعرض دور كل Agent.
• يعرض ما تم تفويضه وما تم استلامه.
• لا يُظهر الوكيل على أنه يعمل إذا كانت جلسته متوقفة.
• Agent Runtime يملك هوية الوكيل والجلسة.
• Swarm يملك توزيع العمل والتنسيق.
• Chat يعرض النتائج ويتيح التفاعل معها.

───

Screen 11 — Memory & Context Inspector

┌────────────────────────────────┐
│ ← Context Inspector │
├────────────────────────────────┤
│ Context Used │
│ │
│ Conversation Messages │
│ Attached Files │
│ Project Context │
│ Memory References │
│ Retrieved Sources │
│ │
│ Included / Excluded │
│ Token Budget │
│ │
│ Source Details │
└────────────────────────────────┘

• يوضح السياق المستخدم فعليًا.
• يميز بين محادثة حالية وذاكرة ومصدر خارجي.
• يوضح العناصر المستبعدة.
• يعرض تقدير استهلاك السياق عندما يتوفر.
• لا يعرض أسرار Vault.
• لا يتيح توسيع السياق دون احترام سياسة الوصول.

───

Screen 12 — Message Details & Attribution

┌────────────────────────────────┐
│ ← Message Details │
├────────────────────────────────┤
│ Message ID │
│ Message Status │
│ │
│ Provider │
│ Model │
│ Execution: Local / Cloud │
│ Role / Contribution │
│ │
│ Generation Started │
│ Generation Completed │
│ │
│ Tool Calls │
│ Source References │
│ Verification Status │
└────────────────────────────────┘

هذه الشاشة تطبق متطلب Attribution على مستوى المنصة:

• Provider وModel.
• Local/Cloud.
• Role/Contribution.
• Timestamps.
• Status.
• Context References.
• Output References.
• Verification.

الواجهة العادية تعرض الحد الأدنى، والتفاصيل تظهر عند الطلب.

───

Screen 13 — Chat Settings

┌────────────────────────────────┐
│ ← Chat Settings │
├────────────────────────────────┤
│ Default Provider / Model │
│ Response Preferences │
│ Streaming │
│ Context & Memory │
│ Tool Permissions │
│ Privacy │
│ Conversation Defaults │
│ Export / Data │
│ Reset │
└────────────────────────────────┘

• كل إعداد له قيمة حالية ومصدر.
• لا تُغير الإعدادات العامة عند تعديل إعداد محادثة واحدة.
• Reset يوضح نطاقه.
• الإعدادات الحساسة تتطلب مراجعة واضحة.

───

Screen 14 — Export & Share

┌────────────────────────────────┐
│ ← Export / Share │
├────────────────────────────────┤
│ Export Format │
│ Markdown / PDF / Text │
│ │
│ Include │
│ Messages / Sources / Files │
│ Model Attribution │
│ Tool Activity │
│ │
│ Privacy Review │
│ │
├────────────────────────────────┤
│ Create Export │
└────────────────────────────────┘

• اختيار المحتوى صريح.
• استبعاد الأسرار والبيانات المحظورة.
• توضيح المرفقات التي ستُضمن.
• مشاركة رابط لا تعني تغيير ملكية الملف أو صلاحياته.
• تسجيل عملية التصدير عند الحاجة.

───

Screen 15 — Error & Recovery

┌────────────────────────────────┐
│ Conversation Recovery │
├────────────────────────────────┤
│ What happened │
│ Error Code / Operation │
│ │
│ Last Confirmed State │
│ │
│ Available Actions │
│ Retry / Resume / Cancel │
│ │
│ Lost or Pending Content │
│ │
└────────────────────────────────┘

• يوضح آخر حالة مؤكدة.
• يفرق بين انقطاع الاتصال وفشل النموذج وفشل الأداة.
• لا يعيد إرسال طلب غير Idempotent تلقائيًا.
• يحتفظ بالرسائل التي تم تأكيد حفظها.
• يوضح إن كانت هناك نتيجة غير مؤكدة.

───

4. بنية التنقل

المصدر| الوجهة| ما يجب الحفاظ عليه
Chat Home| Conversation Workspace| Conversation ID
Workspace| Model Selection| Conversation Context
Workspace| Attachments| Draft Message
Workspace| Tool Activity| Operation ID
Workspace| Context Inspector| Context Snapshot
Workspace| Message Details| Message ID
Workspace| Model Comparison| Prompt / Context Scope
Workspace| Agent Conversation| Task ID
Any Chat Screen| Export| Selected Conversation
Recovery| Workspace| Last Confirmed State

───

5. تكامل Chat

Chat UI
│
▼
Chat Runtime
│
▼
Universal Kernel / KernelHost
├── Provider Registry
├── Model Runtime
├── Context & Memory Services
├── Capability Resolution
├── Policy & Approval
├── Agent Runtime
├── Workflow Runtime
├── Swarm
├── Evidence & Verification
└── Artifact Services

• Chat Runtime يملك المحادثات والرسائل.
• Model Runtime ينفذ طلبات النماذج.
• Agent Runtime يملك الوكلاء والجلسات.
• Workflow Runtime يملك تسلسل خطوات Workflow.
• Swarm يملك التنسيق متعدد الوكلاء.
• Memory Services توفر السياق المصرح به.
• Kernel يحكم الصلاحيات والقدرات.

───

6. اختبارات القبول

• إنشاء واستعادة المحادثات.
• حفظ ترتيب الرسائل.
• Streaming وCancellation.
• تبديل النموذج.
• Model Attribution.
• Context Assembly.
• Memory Isolation.
• Attachments Safety.
• Tool Invocation.
• Tool Approval.
• Tool Failure.
• Multi-model Comparison.
• Agent Handover.
• Export Privacy.
• Offline Recovery.
• Duplicate Request Prevention.
• RTL/LTR وResponsive.
• Accessibility.
• Cross-engine Integration.

شرط الاعتماد: لا تعتبر Chat مكتملًا بمجرد عرض الرسائل. يجب أن تعمل المحادثات والنماذج والسياق والأدوات والاستعادة فعليًا، وأن تحتفظ كل نتيجة بمصدرها وحالتها.
