Mimir Intelligence — Complete Engineering Wireframes & Interaction Specification

1. الغرض والمرجعية

هذه الوثيقة تحول التصور الشامل المعتمد سابقًا لـ Mimir Intelligence إلى مخططات هندسية قابلة للتنفيذ.

لا تعيد تصميم Intelligence ولا تغير تقسيمه الوظيفي.

المساحات الأربع المعتمدة هي:

المساحة| المسؤولية
Brain| Knowledge / Memory / Retrieval
Files| File Manager / Storage / Operations
Downloads| Queue / Downloads / Media Capture
Secure Vault| Passwords / Passkeys / Secrets / Security

ويضاف إليها:

• Intelligence Home موحدة.
• تنقل داخلي سياقي.
• بحث موحد.
• نشاط موحد.
• تكامل عبر الخدمات المشتركة.
• استقلال وظيفي لكل مساحة.

وثائق Intelligence المعمارية والوظيفية السابقة تظل مرجعًا قائمًا. هذه الوثيقة تضيف طبقة المخططات الهندسية، ولا تستبدل تلك الوثائق.

───

2. القواعد الهندسية المشتركة

2.1 بنية Intelligence

MIMIR INTELLIGENCE
│
▼
INTELLIGENCE HOME
│
├── Brain
│ ├── Knowledge
│ ├── Memory
│ └── Retrieval
│
├── Files
│ ├── File Manager
│ ├── Storage
│ └── File Operations
│
├── Downloads
│ ├── Download Queue
│ ├── Download Manager
│ └── Media Capture
│
└── Secure Vault
├── Passwords
├── Passkeys
├── Secrets
└── Security

الخدمات المشتركة لا تعني دمج ملكية البيانات. كل مساحة تحتفظ بمسؤولياتها ومصادر الحقيقة الخاصة بها.

2.2 قياسات الهاتف

الخاصية| القيمة المرجعية
عرض الهاتف| 360dp
الهامش الأفقي| 16dp
Header| 56dp
الحد الأدنى للمس| 48dp
المسافة بين المجموعات| 16dp
المسافة داخل المجموعة| 8dp
بطاقات المحتوى| عرض متجاوب
مساحة التمرير| المحتوى المتبقي

2.3 قواعد العرض

• Intelligence Home تعرض ملخصًا موحدًا، لا تكرر تفاصيل كل مساحة.
• لكل مساحة صفحة رئيسية وهوية وظيفية واضحة.
• التنقل الداخلي يعرض السياق الحالي ولا يفرض إعادة المستخدم إلى Home في كل مرة.
• البحث الموحد يجمع النتائج، لكنه يحتفظ بنوع كل نتيجة ومصدرها.
• النشاط الموحد يعرض الأحداث، لكنه لا يصبح مالكًا لتنفيذ العمليات.
• الإجراءات الحساسة في Secure Vault لا تعرض محتوى الأسرار في القوائم.
• كل مساحة يجب أن تعمل بشكل مستقل حتى لو كانت خدمة مشتركة أخرى غير متاحة.

───

3. Intelligence Home

Screen 01 — Intelligence Home

الهدف: نقطة دخول موحدة تعرض حالة المساحات الأربع وتوفر وصولًا سريعًا إليها.

مخطط الهاتف

┌────────────────────────────────┐
│ Mimir Intelligence Search │
├────────────────────────────────┤
│ │
│ Intelligence Overview │
│ │
│ ┌────────────────────────────┐ │
│ │ Brain │ │
│ │ Knowledge / Memory Status │ │
│ │ Open Brain │ │
│ └────────────────────────────┘ │
│ │
│ ┌────────────────────────────┐ │
│ │ Files │ │
│ │ Storage / Recent Files │ │
│ │ Open Files │ │
│ └────────────────────────────┘ │
│ │
│ ┌────────────────────────────┐ │
│ │ Downloads │ │
│ │ Active Queue / Completed │ │
│ │ Open Downloads │ │
│ └────────────────────────────┘ │
│ │
│ ┌────────────────────────────┐ │
│ │ Secure Vault │ │
│ │ Security Status │ │
│ │ Open Vault │ │
│ └────────────────────────────┘ │
│ │
│ Recent Intelligence Activity │
│ │
├────────────────────────────────┤
│ Home Projects Tasks More │
└────────────────────────────────┘

مكونات الشاشة

Header

• Intelligence Title.
• Unified Search.
• Contextual Menu.

Overview

• Brain Summary.
• Files Summary.
• Downloads Summary.
• Secure Vault Summary.

Recent Activity

• آخر الأحداث المسموح بعرضها.
• نوع الحدث.
• المساحة المالكة.
• وقت الحدث.
• رابط إلى العنصر المرتبط.

قواعد مهمة

• لا تعرض عددًا أو حالة غير مؤكدة.
• لا تعرض أسماء أسرار أو كلمات مرور في Secure Vault Summary.
• لا تعرض الملفات الخاصة دون صلاحية.
• لا تجعل فشل مساحة واحدة يؤدي إلى إخفاء بقية المساحات.
• كل بطاقة تقرأ بياناتها من المساحة المالكة لها.

التفاعلات

• فتح Brain.
• فتح Files.
• فتح Downloads.
• فتح Secure Vault.
• Unified Search.
• Unified Activity.
• الانتقال إلى عنصر حديث.

───

4. Shared Intelligence Navigation

Screen 02 — Internal Navigation

الهدف: التنقل بين المساحات الأربع مع الحفاظ على استقلالها وسياقها.

الهاتف

┌────────────────────────────────┐
│ ← Intelligence Search ⋮ │
├────────────────────────────────┤
│ [Brain] [Files] [Downloads] │
│ [Secure Vault] │
├────────────────────────────────┤
│ │
│ Current Space Content │
│ │
│ │
│ │
└────────────────────────────────┘

التكيف

• Mobile: شريط مساحات أفقي قابل للتمرير أو قائمة سياقية حسب عرض الجهاز.
• Tablet: Navigation Rail أو Sidebar.
• Desktop: Sidebar ثابت مع منطقة محتوى مركزية.

القواعد

• يظهر اسم المساحة الحالية.
• لا يختلط سياق مساحة بأخرى.
• العودة تحفظ حالة الصفحة السابقة.
• الانتقال لا يكرر العمليات النشطة.
• لا ينتقل Secret Context إلى مساحة أخرى تلقائيًا.

───

5. Unified Intelligence Search

Screen 03 — Unified Search

الهدف: البحث عبر المساحات الأربع مع الحفاظ على هوية المصدر ونوع المحتوى.

مخطط الهاتف

┌────────────────────────────────┐
│ ← Intelligence Search │
├────────────────────────────────┤
│ ┌────────────────────────────┐ │
│ │ Search all Intelligence │ │
│ └────────────────────────────┘ │
│ │
│ Scope │
│ [All] [Brain] [Files] │
│ [Downloads] [Vault] │
│ │
│ Filters │
│ Type / Date / Source │
│ │
│ Results │
│ ┌────────────────────────────┐ │
│ │ Result Title │ │
│ │ Type / Owning Space │ │
│ │ Match Summary │ │
│ │ Open │ │
│ └────────────────────────────┘ │
│ │
└────────────────────────────────┘

قواعد النتائج

كل نتيجة تتضمن:

• Result ID.
• Source Space.
• Content Type.
• Title.
• Match Summary.
• Permission State.
• Source Reference.
• Updated At.

التفاعلات

• بحث شامل.
• البحث داخل مساحة واحدة.
• تصفية حسب النوع والتاريخ.
• فتح النتيجة في المساحة المالكة.
• عرض نتائج جزئية عند تعذر البحث في مصدر.
• إظهار أن مصدرًا غير متاح بدل اختلاق نتائج.

الخصوصية

• Vault Search لا يعرض قيم الأسرار.
• الملفات المقيدة لا تظهر دون صلاحية.
• Memory Search يحترم نطاق المستخدم.
• النتائج لا تُرسل تلقائيًا إلى النماذج.

───

6. Unified Intelligence Activity

Screen 04 — Intelligence Activity

الهدف: عرض الأحداث والعمليات عبر Intelligence دون دمج ملكية العمليات.

مخطط الهاتف

┌────────────────────────────────┐
│ ← Intelligence Activity │
├────────────────────────────────┤
│ [All] [Brain] [Files] │
│ [Downloads] [Vault] │
│ │
│ Today │
│ ┌────────────────────────────┐ │
│ │ Activity Description │ │
│ │ Owning Space │ │
│ │ Time / Status │ │
│ │ Open Details │ │
│ └────────────────────────────┘ │
│ │
│ Earlier │
│ ... │
│ │
└────────────────────────────────┘

التفاعلات

• تصفية حسب المساحة.
• فتح تفاصيل الحدث.
• فتح العنصر المرتبط.
• عرض العملية النشطة.
• الانتقال إلى شاشة Diagnostics الخاصة بالمساحة عند حدوث خطأ.

القواعد

• Activity يقرأ الأحداث من مصادرها.
• لا ينفذ Retry بنفسه.
• لا يغير حالة Download أو File Operation أو Memory Job.
• كل حدث يحتفظ بمصدره ومعرفه ووقته وحالته.

───

7. Brain — Knowledge / Memory / Retrieval

المسؤولية

Brain مسؤول عن المعرفة والذاكرة والاسترجاع، وليس مدير الملفات أو مدير كلمات المرور.

Brain Home

┌────────────────────────────────┐
│ ← Brain Search │
├────────────────────────────────┤
│ Brain Overview │
│ Knowledge / Memory Health │
│ │
│ ┌────────────────────────────┐ │
│ │ Knowledge │ │
│ │ Sources / Collections │ │
│ │ Open Knowledge │ │
│ └────────────────────────────┘ │
│ │
│ ┌────────────────────────────┐ │
│ │ Memory │ │
│ │ Memory Types / Status │ │
│ │ Open Memory │ │
│ └────────────────────────────┘ │
│ │
│ ┌────────────────────────────┐ │
│ │ Retrieval │ │
│ │ Search / Recent Queries │ │
│ │ Open Retrieval │ │
│ └────────────────────────────┘ │
│ │
│ Recent Brain Activity │
└────────────────────────────────┘

Screen 05 — Brain Home

المكونات:

• Knowledge Overview.
• Memory Overview.
• Retrieval Overview.
• Recent Brain Activity.
• Indexing/Processing Status.

التفاعلات:

• فتح Knowledge.
• فتح Memory.
• فتح Retrieval.
• البحث.
• مراجعة حالة المعالجة.

───

Screen 06 — Knowledge Library

┌────────────────────────────────┐
│ ← Knowledge Library Search │
├────────────────────────────────┤
│ Collections │
│ [All] [Projects] [Personal] │
│ │
│ Sources │
│ ┌────────────────────────────┐ │
│ │ Source Name │ │
│ │ Type / Origin │ │
│ │ Index Status │ │
│ │ Open / ⋮ │ │
│ └────────────────────────────┘ │
│ │
│ ... │
├────────────────────────────────┤
│ + Add Knowledge Source │
└────────────────────────────────┘

المكونات:

• Collection List.
• Knowledge Source Cards.
• Source Type.
• Origin Reference.
• Index Status.
• Last Updated.
• Permissions.

التفاعلات:

• إضافة مصدر معرفة.
• ربط ملف أو مصدر مسموح.
• إنشاء Collection.
• إزالة المصدر من Knowledge Index دون حذف الملف الأصلي.
• إعادة الفهرسة.
• عرض تفاصيل المصدر.

الحالات: Imported، Processing، Indexed، Partial، Failed، Restricted، Outdated.

───

Screen 07 — Knowledge Source Details

┌────────────────────────────────┐
│ ← Knowledge Source │
├────────────────────────────────┤
│ Source Identity │
│ Origin / Type / Owner │
│ │
│ Indexing Status │
│ Last Indexed │
│ │
│ Extracted Content │
│ Sections / Metadata │
│ │
│ Retrieval References │
│ │
│ Permissions / Provenance │
├────────────────────────────────┤
│ Re-index Remove from Brain │
└────────────────────────────────┘

• يعرض مصدر المعرفة ومرجعه.
• يعرض ما استُخرج منه.
• يعرض حالة الفهرسة.
• يحتفظ بمصدر كل مقطع مسترجع.
• إزالة المصدر من Brain لا تحذف الملف الأصلي من Files.

───

Screen 08 — Memory Manager

┌────────────────────────────────┐
│ ← Memory Manager Search │
├────────────────────────────────┤
│ Memory Overview │
│ │
│ [Short-term] [Long-term] │
│ [Episodic] [Semantic] │
│ [Procedural] │
│ │
│ Memory Items │
│ ┌────────────────────────────┐ │
│ │ Memory Summary │ │
│ │ Type / Source │ │
│ │ Created / Updated │ │
│ │ Details │ │
│ └────────────────────────────┘ │
│ │
└────────────────────────────────┘

الأنواع:

• Short-term / Context Memory.
• Long-term Memory.
• Episodic Memory.
• Semantic Memory.
• Procedural Memory.

التفاعلات:

• تصفح حسب النوع.
• عرض المصدر.
• مراجعة استخدام الذاكرة.
• تعديل أو تصحيح العناصر وفق سياسات المنتج.
• التحكم في نطاق الاستخدام.
• مراجعة العناصر المرتبطة بمشروع أو محادثة.

ضوابط:

• لا تُعامل جميع أنواع الذاكرة على أنها نوع تخزين واحد.
• لا تعرض بيانات حساسة في ملخصات عامة.
• لا تشارك الذاكرة تلقائيًا مع كل محرك.
• أي استخدام خارج Brain يحتاج Context Package وصلاحية واضحة.

───

Screen 09 — Retrieval Workspace

┌────────────────────────────────┐
│ ← Retrieval Workspace │
├────────────────────────────────┤
│ Query │
│ ┌────────────────────────────┐ │
│ │ Search knowledge... │ │
│ └────────────────────────────┘ │
│ │
│ Search Scope │
│ Collections / Memory / Sources │
│ │
│ Retrieval Settings │
│ │
│ Results │
│ ┌────────────────────────────┐ │
│ │ Retrieved Passage │ │
│ │ Source / Relevance │ │
│ │ Open Source │ │
│ └────────────────────────────┘ │
│ │
│ Context Preview │
└────────────────────────────────┘

التفاعلات:

• تنفيذ Query.
• تحديد مصادر البحث.
• مراجعة المقاطع.
• فتح المصدر.
• اختيار المقاطع لتكوين Context Package.
• إرسال السياق إلى Chat أو Agent عند طلب المستخدم.

ضوابط:

• Relevance Score ليس دليل صحة.
• الاحتفاظ بمراجع المصادر.
• عدم إخفاء المصادر المتعارضة.
• عدم تحويل نتائج Retrieval إلى حقائق مؤكدة دون تحقق.

───

8. Files — File Manager / Storage / Operations

المسؤولية

Files هي المساحة المالكة لتصفح الملفات والمجلدات وإدارة التخزين والعمليات الملفية المشتركة.

لا تنشئ Forge أو Terminal مدير ملفات منافسًا؛ يمكنهما عرض ملفات نطاقهما عبر عقود Files.

Screen 10 — Files Home

┌────────────────────────────────┐
│ ← Files Search │
├────────────────────────────────┤
│ Storage Overview │
│ Used / Available / Sources │
│ │
│ Locations │
│ [Recent] [Local] [Cloud] │
│ [Projects] [Shared] │
│ │
│ Categories │
│ Documents / Images / Video │
│ Archives / Other │
│ │
│ Recent Files │
│ ┌────────────────────────────┐ │
│ │ File Name │ │
│ │ Type / Size / Updated │ │
│ └────────────────────────────┘ │
│ │
└────────────────────────────────┘

التفاعلات:

• فتح Location.
• البحث.
• تصفح الفئات.
• عرض التخزين.
• إنشاء مجلد.
• رفع أو استيراد ملف وفق دعم المصدر.

───

Screen 11 — File Browser

┌────────────────────────────────┐
│ ← Files / Folder Name ⋮ │
├────────────────────────────────┤
│ Breadcrumb Path │
│ │
│ [Sort] [Filter] [View] │
│ │
│ ┌────────────────────────────┐ │
│ │ Folder Name │ │
│ │ Item Count │ │
│ └────────────────────────────┘ │
│ │
│ ┌────────────────────────────┐ │
│ │ File Name │ │
│ │ Type / Size / Date │ │
│ └────────────────────────────┘ │
│ │
│ ... │
├────────────────────────────────┤
│ + New / Import │
└────────────────────────────────┘

المكونات:

• Breadcrumb.
• Folder List.
• File List.
• Sort/Filter.
• Grid/List View.
• Multi-select.
• Context Menu.

التفاعلات:

• فتح ملف أو مجلد.
• تحديد متعدد.
• نقل.
• نسخ.
• إعادة تسمية.
• حذف.
• مشاركة.
• عرض Metadata.
• إنشاء مجلد.

───

Screen 12 — File Details & Preview

┌────────────────────────────────┐
│ ← File Details ⋮ │
├────────────────────────────────┤
│ File Preview │
│ │
│ File Name │
│ Type / Size │
│ │
│ Location │
│ Created / Modified │
│ │
│ Integrity / Version │
│ Access / Sharing │
│ │
│ Related Activity │
├────────────────────────────────┤
│ Open Share More │
└────────────────────────────────┘

• Preview حسب نوع الملف ودعم المنصة.
• Metadata.
• Version/Integrity عند توفرها.
• مصدر الملف.
• صلاحيات الوصول.
• Activity المرتبطة.
• لا تعرض الملف على أنه سليم دون نتيجة تحقق فعلية.

───

Screen 13 — Storage & Locations

┌────────────────────────────────┐
│ ← Storage & Locations │
├────────────────────────────────┤
│ Storage Summary │
│ Used / Available │
│ │
│ Storage Locations │
│ Local │
│ Project Storage │
│ Connected Cloud │
│ Shared Locations │
│ │
│ Storage Usage │
│ Category Breakdown │
│ │
│ Sync / Availability │
└────────────────────────────────┘

التفاعلات:

• فتح موقع.
• مراجعة حالة الاتصال.
• مراجعة المساحة.
• مزامنة عند توفرها.
• فصل مصدر تخزين بعد مراجعة أثره.

قواعد:

• Connected لا تعني Synced.
• Synced لا تعني أن كل الملفات محدثة.
• لا تعرض المساحة المتاحة دون قراءة موثوقة.

───

Screen 14 — File Operations

┌────────────────────────────────┐
│ ← File Operations │
├────────────────────────────────┤
│ Active Operations │
│ │
│ ┌────────────────────────────┐ │
│ │ Operation Type │ │
│ │ Source / Destination │ │
│ │ Progress / State │ │
│ │ Pause / Cancel / Details │ │
│ └────────────────────────────┘ │
│ │
│ Completed │
│ Failed │
│ │
│ Operation History │
└────────────────────────────────┘

التفاعلات:

• متابعة Copy/Move/Import/Export.
• إيقاف أو إلغاء إذا كان Runtime يدعم.
• عرض سبب الفشل.
• إعادة المحاولة عند السماح.
• مراجعة النتائج.

قواعد:

• لا تعرض نجاح نقل ملف قبل تأكيد الوجهة.
• لا تحذف المصدر قبل نجاح النقل والتحقق عند الحاجة.
• لا تعيد عملية غير آمنة تلقائيًا.

───

9. Downloads — Queue / Downloads / Media Capture

المسؤولية

Downloads مسؤولة عن إدارة عمليات التنزيل ومخرجاتها وعمليات الالتقاط الإعلامي. لا تملك Browser Navigation أو إدارة الملفات العامة.

Screen 15 — Downloads Home

┌────────────────────────────────┐
│ ← Downloads Search │
├────────────────────────────────┤
│ Active Queue │
│ Active / Waiting / Failed │
│ │
│ ┌────────────────────────────┐ │
│ │ Download Item │ │
│ │ Source / File Name │ │
│ │ Progress / State │ │
│ │ Pause / Cancel │ │
│ └────────────────────────────┘ │
│ │
│ Completed Downloads │
│ │
│ Media Captures │
│ │
└────────────────────────────────┘

المكونات:

• Queue Summary.
• Active Downloads.
• Waiting Items.
• Failed Items.
• Completed Items.
• Media Captures.

───

Screen 16 — Download Queue

┌────────────────────────────────┐
│ ← Download Queue Filter │
├────────────────────────────────┤
│ [Active] [Waiting] [Failed] │
│ │
│ Queue Item │
│ Source / Destination │
│ Progress / Speed / Size │
│ Priority / State │
│ │
│ Queue Item │
│ ... │
└────────────────────────────────┘

التفاعلات:

• Pause.
• Resume.
• Cancel.
• Reorder إذا كان مدعومًا.
• Retry بعد مراجعة السبب.
• فتح مصدر التنزيل.

───

Screen 17 — Download Details

┌────────────────────────────────┐
│ ← Download Details │
├────────────────────────────────┤
│ File Name │
│ Source URL / Domain │
│ │
│ Download Status │
│ Progress / Size / Speed │
│ │
│ Destination │
│ Integrity / Verification │
│ │
│ Error Details │
│ │
├────────────────────────────────┤
│ Open / Locate / Share │
└────────────────────────────────┘

القواعد:

• عرض المصدر.
• عرض الوجهة.
• عرض حالة الملف.
• التفريق بين Download Complete وIntegrity Verified.
• مشاركة الملف عبر Files.
• حذف سجل التنزيل لا يعني حذف الملف الفعلي.

───

Screen 18 — Media Capture

┌────────────────────────────────┐
│ ← Media Capture + │
├────────────────────────────────┤
│ Capture Sources │
│ Browser / Supported Sources │
│ │
│ Capture Queue │
│ ┌────────────────────────────┐ │
│ │ Media Item │ │
│ │ Source / Type │ │
│ │ Capture State │ │
│ └────────────────────────────┘ │
│ │
│ Saved Captures │
│ │
│ Capture Settings │
└────────────────────────────────┘

التفاعلات:

• استقبال Capture Request من مصدر مسموح.
• مراجعة المصدر ونوع المحتوى.
• تنفيذ الالتقاط عبر Adapter معتمد.
• حفظ الناتج في Files.
• ربط الناتج بمصدره.
• عرض حالة فشل الالتقاط.

ضوابط:

• لا يتجاوز الالتقاط صلاحيات المصدر.
• لا يلتقط محتوى محميًا أو مقيدًا بطريقة تتجاوز الحماية.
• لا يعرض Media Capture كتنزيل ناجح إذا لم يوجد Artifact صالح.

───

10. Secure Vault — Passwords / Passkeys / Secrets / Security

المسؤولية

Secure Vault هو المكان الوحيد المسؤول عن تخزين وإدارة الأسرار وبيانات الاعتماد. لا يُسمح لأي مساحة أخرى بأن تصبح مخزنًا بديلًا للأسرار.

قواعد واجهة صارمة

• إخفاء القيم الحساسة افتراضيًا.
• المصادقة قبل كشف قيمة سرية.
• عدم إرسال الأسرار الخام إلى نماذج الذكاء الاصطناعي.
• استخدام Secret References محدودة النطاق بدل القيم الخام.
• تسجيل عمليات الوصول والتعديل.
• فصل بيانات الاعتماد عن ملفات المستخدم العادية.

Screen 19 — Secure Vault Home

┌────────────────────────────────┐
│ ← Secure Vault Locked │
├────────────────────────────────┤
│ Security Overview │
│ Vault Status │
│ │
│ ┌────────────────────────────┐ │
│ │ Passwords │ │
│ │ Stored Credentials │ │
│ │ Open │ │
│ └────────────────────────────┘ │
│ │
│ ┌────────────────────────────┐ │
│ │ Passkeys │ │
│ │ Registered Credentials │ │
│ │ Open │ │
│ └────────────────────────────┘ │
│ │
│ ┌────────────────────────────┐ │
│ │ Secrets │ │
│ │ API Keys / Tokens │ │
│ │ Open │ │
│ └────────────────────────────┘ │
│ │
│ Security Activity │
└────────────────────────────────┘

التفاعلات:

• Unlock.
• فتح Passwords.
• فتح Passkeys.
• فتح Secrets.
• Security Review.
• Activity.

───

Screen 20 — Vault Authentication / Unlock

┌────────────────────────────────┐
│ Secure Vault │
├────────────────────────────────┤
│ │
│ Vault Locked │
│ │
│ Authenticate to Continue │
│ │
│ Biometrics / Device Auth │
│ PIN / Recovery Method │
│ │
│ Security Status │
│ │
└────────────────────────────────┘

القواعد:

• المصادقة حسب قدرات المنصة وسياسة Vault.
• لا تخزن الواجهة رمز المصادقة.
• لا تعرض محتوى Vault قبل نجاح المصادقة.
• قفل تلقائي وفق السياسة.
• انتهاء صلاحية الجلسة يعيد حالة القفل.
• لا تعتبر فتح الواجهة دليلًا على نجاح فتح Vault.

───

Screen 21 — Password Manager

┌────────────────────────────────┐
│ ← Passwords Search │
├────────────────────────────────┤
│ Categories │
│ [All] [Personal] [Work] │
│ │
│ ┌────────────────────────────┐ │
│ │ Website / Account │ │
│ │ Username (partially masked) │ │
│ │ Password •••••••• │ │
│ │ Details ⋮ │ │
│ └────────────────────────────┘ │
│ │
│ ... │
├────────────────────────────────┤
│ + Add Password │
└────────────────────────────────┘

التفاعلات:

• إضافة بيانات اعتماد.
• تعديل.
• حذف.
• كشف كلمة المرور بعد المصادقة.
• نسخ محدود المدة وفق السياسة.
• تعبئة آمنة في Browser عند طلب المستخدم.
• إنشاء بيانات اعتماد جديدة عند دعم العملية.

ضوابط:

• لا تعرض كلمات المرور في نتائج البحث.
• لا تنسخ القيمة إلى Clipboard دون إجراء صريح.
• لا ترسلها إلى Chat أو Browser AI كجزء من Prompt.
• تعبئة المتصفح تتم عبر Secret Reference أو Secure Autofill Adapter.

───

Screen 22 — Passkeys

┌────────────────────────────────┐
│ ← Passkeys Search │
├────────────────────────────────┤
│ Registered Passkeys │
│ │
│ ┌────────────────────────────┐ │
│ │ Website / Relying Party │ │
│ │ Account │ │
│ │ Device / Status │ │
│ │ Details │ │
│ └────────────────────────────┘ │
│ │
│ Registration / Recovery Info │
└────────────────────────────────┘

التفاعلات:

• عرض Passkey Metadata.
• إنشاء Passkey عبر منصة معتمدة.
• مراجعة الجهاز أو المصدر.
• إزالة تسجيل وفق قواعد WebAuthn/المنصة.
• عرض حالة الاعتماد.

قواعد:

• لا تعامل Passkeys ككلمات مرور عادية.
• لا تعرض Private Key.
• لا تدعي إمكانية التصدير إذا لم تدعمها التقنية.
• التحقق من هوية الموقع وRelying Party.

───

Screen 23 — Secrets Manager

┌────────────────────────────────┐
│ ← Secrets Search │
├────────────────────────────────┤
│ Secret Categories │
│ API Keys / Tokens / Other │
│ │
│ ┌────────────────────────────┐ │
│ │ Secret Name │ │
│ │ Owner / Scope │ │
│ │ Value: Masked │ │
│ │ Updated / Expiry │ │
│ │ Details │ │
│ └────────────────────────────┘ │
│ │
├────────────────────────────────┤
│ + Add Secret │
└────────────────────────────────┘

التفاعلات:

• إضافة Secret.
• تعديل.
• تدوير.
• إبطال.
• تحديد نطاق الاستخدام.
• تحديد صلاحية أو انتهاء.
• ربط Secret بمشروع أو عملية مسموح بها.
• إصدار Reference لاستخدامه عبر Kernel.

قواعد:

• القيمة الخام لا تدخل Context Package.
• Secret Reference لا يمنح صلاحية مفتوحة.
• يجب تحديد الجهة والعملية والنطاق والمدة.
• إبطال Secret يجب أن يمنع الاستخدامات اللاحقة.

───

Screen 24 — Vault Security Center

┌────────────────────────────────┐
│ ← Vault Security Center │
├────────────────────────────────┤
│ Security Overview │
│ Vault Lock / Authentication │
│ │
│ Security Recommendations │
│ │
│ Exposed / Expiring Items │
│ │
│ Access Permissions │
│ Connected Services │
│ │
│ Recent Access Events │
│ │
│ Recovery & Emergency Actions │
└────────────────────────────────┘

التفاعلات:

• مراجعة صلاحيات الوصول.
• إبطال Reference.
• مراجعة استخدام Secret.
• مراجعة عناصر منتهية أو معرضة للخطر عند توفر دليل.
• قفل Vault.
• مراجعة نشاط الوصول.

───

Screen 25 — Vault Access & Approval

┌────────────────────────────────┐
│ ← Vault Access Request │
├────────────────────────────────┤
│ Requesting Engine / Agent │
│ Requested Secret Reference │
│ Purpose │
│ Target Operation │
│ │
│ Scope / Duration │
│ Risk / Policy │
│ │
│ [Approve Once] [Deny] │
└────────────────────────────────┘

التفاعلات:

• مراجعة الطلب.
• السماح لمرة واحدة أو رفض.
• تحديد نطاق الصلاحية.
• إبطال صلاحية قائمة.
• مراجعة سجل الاستخدام.

قواعد إلزامية:

• لا يوجد Secret Access شامل افتراضيًا.
• الموافقة مرتبطة بالفاعل والعملية والهدف والمدة.
• لا يسمح للنموذج بقراءة القيمة لمجرد أنه طلبها.
• Kernel Policy هو صاحب قرار السماح.
• Vault ينفذ الوصول الآمن فقط بعد اجتياز السياسة.

───

11. الانتقالات بين Intelligence والمساحات الأربع

المصدر| الوجهة| البيانات التي تنتقل
Intelligence Home| Brain| Brain Space Reference
Intelligence Home| Files| Files Context
Intelligence Home| Downloads| Queue/Download Context
Intelligence Home| Secure Vault| Vault Navigation Context دون أسرار
Brain| Files| Source Reference
Files| Brain| Knowledge Ingestion Request
Downloads| Files| Artifact Reference
Browser| Downloads| Authorized Download Request
Browser| Files| Saved File Reference
Chat| Brain| Scoped Retrieval Request
Chat| Vault| Secret Reference Request عبر Kernel فقط
Forge| Files| Project File References
Forge| Vault| Scoped Secret Reference
Terminal| Vault| Execution-scoped Secret Reference

قاعدة: الانتقال ينقل Reference أو Request مصرحًا به، ولا ينقل نسخة غير مقيدة من البيانات أو الأسرار.

───

12. التكامل المعماري

MIMIR SHELL
│
▼
INTELLIGENCE HOME
│
SHARED NAVIGATION
│
▼
UNIVERSAL KERNEL
│
┌─────────────┼──────────────┐
│ │ │
▼ ▼ ▼
BRAIN RUNTIME FILES RUNTIME DOWNLOAD RUNTIME
│ │ │
└─────────────┼──────────────┘
│
▼
VAULT RUNTIME
│
▼
POLICY / APPROVAL
│
▼
AUDIT / EVIDENCE

هذا رسم منطقي لمسارات التكامل، وليس ترتيبًا إلزاميًا لاستدعاء كل خدمة في كل عملية.

مسؤوليات العقود

العقد| الوظيفة
Knowledge Source Contract| تسجيل مصادر المعرفة
Memory Contract| قراءة وإدارة الذاكرة
Retrieval Contract| تنفيذ الاسترجاع وإرجاع المراجع
File Contract| قراءة وكتابة وإدارة الملفات
Storage Contract| تعريف مواقع التخزين
File Operation Contract| Copy/Move/Delete/Import/Export
Download Contract| إنشاء ومتابعة عمليات التنزيل
Media Capture Contract| طلبات الالتقاط المصرح بها
Secret Reference Contract| استخدام الأسرار عبر مراجع آمنة
Vault Access Contract| التحكم في الوصول
Audit Contract| تسجيل عمليات الوصول والتغييرات
Context Package Contract| نقل السياق المسموح بين المساحات

يجب مراجعة العقود الحالية قبل إنشاء أي ملف جديد.

───

13. اختبارات القبول

Intelligence Home

• ظهور المساحات الأربع.
• استقلال حالة كل مساحة.
• عدم تعطيل Home بسبب فشل خدمة واحدة.
• التنقل وحفظ السياق.

Brain

• Knowledge Source Ingestion.
• Indexing Status.
• Memory Type Separation.
• Retrieval Accuracy Evaluation.
• Source Attribution.
• Context Isolation.

Files

• File Operations.
• Metadata Accuracy.
• Storage State.
• Copy/Move Integrity.
• Conflict Handling.
• Access Control.

Downloads

• Queue Lifecycle.
• Progress Accuracy.
• Pause/Resume.
• Cancellation.
• Retry Safety.
• Artifact Integrity.
• Source Attribution.

Secure Vault

• Authentication.
• Auto-lock.
• Secret Masking.
• Reference Scoping.
• Approval Enforcement.
• Secret Revocation.
• Audit.
• No Secret Leakage to Model Context.

Cross-space

• Brain إلى Files.
• Files إلى Brain.
• Browser إلى Downloads.
• Downloads إلى Files.
• Chat إلى Brain.
• Forge وTerminal إلى Vault عبر Kernel.
• Search عبر المساحات.
• Activity Aggregation.
• Permission Isolation.

───

14. معيار اكتمال Mimir Intelligence

لا يُعلن اكتمال Intelligence إلا عند إثبات:

1. عمل Intelligence Home.
2. عمل التنقل الداخلي وحفظ الحالة.
3. استقلال Brain وFiles وDownloads وSecure Vault.
4. صحة البحث والنشاط الموحدين.
5. تنفيذ العمليات الحقيقية لكل مساحة.
6. سلامة العقود المشتركة.
7. عدم تسرب البيانات الحساسة بين المساحات.
8. سلامة التكامل مع Browser وChat وForge وTerminal.
9. وجود اختبارات لكل قدرة.
10. وجود أدلة تحقق فعلية.
11. استجابة واجهات الهاتف والتابلت وسطح المكتب.
12. عمل RTL/LTR وإمكانية الوصول.

مبدأ التصميم: Intelligence مساحة موحدة في التجربة، مستقلة في ملكية الوظائف والبيانات، ومتكاملة عبر العقود والخدمات المشتركة.
