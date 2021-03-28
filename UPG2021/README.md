Tento projekt tvoří kostru samostatné práce KIV/UPG 2020/2021. Projekt je k dispozici ke stažení na https://gitlab.kiv.zcu.cz/UPG/Mapa_UPG_2021.git. 

UPOZORNĚNÍ: projekt se v průběhu semestru může měnit (např. rozšíření datové základny). Studentům je doporučeno, aby pro svou samostatnou práci využili verzovací systém Git, a to tak, že mají dva vzdálené repozitáře: vlastní origin pro FETCH/PULL/PUSH a upstream pro FETCH/PULL vedoucí na https://gitlab.kiv.zcu.cz/UPG/Mapa_UPG_2021.git a čas od času provedou FETCH/PULL z upstream.


Návod pro použití pro úplné začátečníky
---------------------------------------
1. Spusťte Eclipse a zvolte Workspace mimo tento adresář (můžete použít Workspace, který používáte pro cvičení)
2. Proveďte import tohoto projektu (File/Import General/Existing Projects into Workspace) - jako root directory vyberte adresář s tímto souborem (adresář obsahuje soubory .project a .classpath)
3. Vypracujte SP dle běžných zvyklostí, přičemž zdrojové kódy umisťujte do adresáře src (případně podadresářů) - vše se vám bude automaticky překládat do adresáře bin
4. Před odevzdáním upravte dávkové soubory MakeDoc.cmd, Build.cmd a Run.cmd příslušným způsobem a vyzkoušejte, že fungují a dělají co mají
5. Celý tento kořenový adresář ZAZIPUJTE a výsledný archiv odevzdejte

Návod pro použití s GIT pro úplné začátečníky
---------------------------------------------
1. Pokud již máte zřízen někde GIT účet umožňující vám zakládat soukromé (private) repozitáře, jděte na krok 3
2. Založte si účet na https://gitlab.com/ nebo alternativně na https://bitbucket.org. V případě, že se rozhodnete pro druhou volbu, uveďte jako emailovou adresu vaši studentskou (tj. @students.zcu.cz). Bitbucket automaticky rozpozná, že zcu.cz je univerzita a nastaví vám režim "akademie", v rámci kterého lze zakládat zdarma neomezený počet soukromých repozitářů, na kterých může pracovat souběžně libovolné množství lidí - to sice v UPG nevyužijete, ale ve vyšším ročníku toto oceníte.
3. Založte nový SOUKROMÝ (private) repozitář a nějak vhodně si ho pojmenujte. Rady, ať založíte .gitignore nebo soubor readme, ignorujte - vy již máte svůj existující projekt.
4. Získejte HTTPS adresu k vašemu repozitáři (bývá zřetelně uvedena).
5. Pokud jste tento projekt získali doporučeným klonováním z Git (máte zde skrytý podadresář .git), jděte na krok 8.
6. Prostřednictvím TortoiseGit (vyvolá se z kontextového menu v průzkumníkovi) nebo Git Extensions (či jiných) založte lokální repozitář v tomto adresáři (Git Create repository here ...). Od této chvíle můžete provádět "commit" a uchovávat lokálně změny.
7. Vyvolejte Git Commit a všechny soubory "commitujte" do lokálního repozitáře (počáteční/první commit).
8. Vyvolejte Push a v nastavení "Remote" přidejte nový vzdálený repozitář s názvem "origin" a jako URL volte tu, kterou jste získali v kroku 4 (tj. HTTPS adresu k vašemu repozitáři). Dokončete Push. TortoiseGit si během toho vyžádá vaše přihlašovací údaje a všechny vaše změny (lokálně vedené v podadresáři .git) zkopíruje do vzdáleného repozitáře.
8. Zkontrolujte, že data jsou skutečně uložena.

Poznámka: využijte soubor .gitignore pro specifikaci automaticky generovaných souborů (.class, javadoc dokumentace, apod.), aby se tyto soubory neukládaly do Git repozitářů.
