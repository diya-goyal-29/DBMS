DROP TABLE IF EXISTS author, non_canonical, publication_venue, research_paper, citations, writtenby;

CREATE TABLE author(
	canonical_name TEXT,
	PRIMARY KEY(canonical_name)
);

CREATE TABLE non_canonical(
	canonical_name TEXT,
	non_canonical_name TEXT,
	FOREIGN KEY(canonical_name) REFERENCES author(canonical_name)
);

ALTER TABLE non_canonical DISABLE TRIGGER ALL;


CREATE TABLE publication_venue(
	venue_name TEXT,
	venue_type TEXT,
	PRIMARY KEY(venue_name)
);


CREATE TABLE research_paper(
	paper_id INT,
	abstract TEXT,
	publication_year INT,
	title TEXT,
	venue_name TEXT,
	corresponding_author_canonical_name Text,
	FOREIGN KEY(corresponding_author_canonical_name) REFERENCES author(canonical_name),
	FOREIGN KEY(venue_name) REFERENCES publication_venue(venue_name),
	PRIMARY KEY(paper_id)
);


ALTER TABLE research_paper DISABLE TRIGGER ALL;


CREATE TABLE citations(
	main_paper_id INT,
	ref_paper_id INT,
	FOREIGN KEY(main_paper_id) REFERENCES research_paper(paper_id),
	FOREIGN KEY(ref_paper_id) REFERENCES research_paper(paper_id)
);

ALTER TABLE citations DISABLE TRIGGER ALL;


CREATE TABLE writtenby(
	paper_id INT, 
	author_name TEXT,
	author_rank INT,
	FOREIGN KEY(paper_id) REFERENCES research_paper(paper_id),
	FOREIGN KEY(author_name) REFERENCES author(canonical_name),
	PRIMARY KEY(paper_id,author_name) 
);

ALTER TABLE writtenby DISABLE TRIGGER ALL;


CREATE OR REPLACE RULE writtenby_ignore_duplicate_inserts AS
    ON INSERT TO writtenby
   WHERE (EXISTS ( SELECT 1
           FROM writtenby
          WHERE writtenby.paper_id = NEW.paper_id AND writtenby.author_name = NEW.author_name )) DO INSTEAD NOTHING;

CREATE OR REPLACE RULE citation_ignore_duplicate_inserts AS
    ON INSERT TO citations
   WHERE NEW.main_paper_id = NEW.ref_paper_id   DO INSTEAD NOTHING;


CREATE OR REPLACE RULE author_duplicate_inserts AS
    ON INSERT TO author
    WHERE (EXISTS ( SELECT 1
           FROM author
          WHERE author.canonical_name = NEW.canonical_name ))DO INSTEAD NOTHING;

CREATE OR REPLACE RULE publication_venue_duplicate_inserts AS
    ON INSERT TO publication_venue
    WHERE (EXISTS ( SELECT 1
           FROM publication_venue
          WHERE publication_venue.venue_name = NEW.venue_name ))DO INSTEAD NOTHING;


CREATE OR REPLACE RULE research_paper_duplicate_inserts AS
    ON INSERT TO research_paper
    WHERE (EXISTS ( SELECT 1
           FROM research_paper
          WHERE research_paper.paper_id = NEW.paper_id))DO INSTEAD NOTHING;
