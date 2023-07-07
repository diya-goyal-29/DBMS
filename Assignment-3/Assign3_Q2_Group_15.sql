select citations.main_paper_id, citations.ref_paper_id, research_paper.abstract, research_paper.publication_year,
research_paper.title, research_paper.venue_name, writtenby.author_name, writtenby.author_rank 
from citations left outer join research_paper on citations.ref_paper_id = research_paper.paper_id 
left outer join writtenby on citations.ref_paper_id = writtenby.paper_id 
order by citations.main_paper_id, writtenby.author_rank;