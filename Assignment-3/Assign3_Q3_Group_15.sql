create view view_name as (select c1.ref_paper_id as X, c2.main_paper_id as Z 
from citations as c1 inner join citations as c2 on c1.main_paper_id = c2.ref_paper_id
and case when c1.ref_paper_id is null then 0 else 1 end = 1 order by X asc);

select research_paper.paper_id as X_id, research_paper.abstract as X_abstract, research_paper.title as X_title, research_paper.publication_year as X_publication_year,
research_paper.venue_name as X_venue_name, research_paper.corresponding_author_canonical_name as X_main_author, r.paper_id as Z_id, r.abstract as Z_abstract, 
r.title as Z_title, r.publication_year as Z_publication_year, r.venue_name as Z_venue_name, r.corresponding_author_canonical_name as Z_main_author
from view_name left join research_paper on view_name.X = research_paper.paper_id left join research_paper as r on view_name.Z = r.paper_id;