select research_paper.paper_id as citee, citations.main_paper_id as citer, 
r.abstract, r.title, r.venue_name, writtenby.author_name, writtenby.author_rank 
from research_paper left join citations on citations.ref_paper_id = research_paper.paper_id 
left join research_paper as r on citations.main_paper_id = r.paper_id 
left join writtenby on writtenby.paper_id = citations.main_paper_id 
order by research_paper.paper_id asc, writtenby.author_rank;