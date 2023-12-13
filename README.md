# Introduction 

! [Albian-framework-orm](. /image/faci.webp)

A "non-java-style" ORM framework from the ORM part of [Albianj-framework](https://github.com/crosg/Albianj2), which I originally developed.

The [Albianj-framework](https://github.com/crosg/Albianj2) was used in my former company for more than 5 years, and had been under enough online pressure to accumulate enough online experience.
In my former company, [Albianj-framework](https://github.com/crosg/Albianj2) accomplished the task of front-end and back-end code framework for a certain business, and supported and completed the development of the business very well.

But in these years of use, found a lot of problems and the initial design of arbitrary places. There are many deficiencies because of the design of the established difficult to change or expand. So I need to start from scratch, refactor + rewrite it.

Now I left my former company, because [Albianj-framework](https://github.com/crosg/Albianj2) has been open-sourced before when I was in the company, and it was also written in 2011 before the company was founded and open-sourced in googlecode before, so there is no copyright problem to re-fork it. So there is no copyright issue in re-forking it.

# Current functionality

Currently Albianj-framework-orm has supported the following features:

- complete ORM functionality
- Entry and Database and Table based on the fast split library and table functionality
- Read-write separation support
- batch save multiple Entry at a time
- Unreliable "pseudo" distributed transactions.

# The final list of features
The final features to be achieved by Albianj-framework-orm are:

- Full ORM functionality
- Fast split-library and split-table capabilities.
- Exception pinpointing and problem solving
- Batch save/update multiple entities to multiple libraries/tables.
- Ability to save one entity to multiple libraries\tables at a time
- Read-write separation support
- Open interface to execute customized sql
- The return data value does not have to be an Entry, but can be a Map.
- SessionId support
- support for join and in operations
- sql statement auditing and limiting functions
- can be integrated with spring , can also be used independently .

# Target

Albianj-framework-orm doesn't have any "big ambitions", it was born out of boredom in 2011 when it was single, and was on the back burner for a long time until 2015, when it was adopted for a variety of reasons. Nowadays, it still exists only as a personal diversion.

There will be no regular scheduling or updating, less when it's busy, more when it's not. The important thing is to have fun coding.

The point is: the project will not continue to expand indefinitely, so it will not form a "functional hodgepodge", it is just a "small but beautiful" typical. This has been my personal "rule" for writing programs, strictly implementing the strategy of "a program is only effective for a part of the requirements". So Albianj-framework-orm will only work well for a certain number of people, and you might be one of those exceptions.

I'm not a java programmer, not when I wrote the Albianj-framework, not now, not later... The evolution has stopped. The classic comment for me is: java is basically not! So a lot of java-style "may" not be observed (or not known, to be precise, which is indeed a sign of ignorance). I just want to make Albianj-framework-orm the ORM I have in mind.

Finally: not updating doesn't mean not maintaining it, and hopefully it will always be maintained.

# Guidelines

These are the guidelines that Albianj-framework-orm follows.
- Refer to as few jar dependencies as possible
- As few configuration files as possible
- Minimize ease of use, especially for environment configuration and so on.
- Make it as easy as possible for the user who needs it.

# Plan

- v1 (in no particular order):
- Remove features that have not been used by albianj-framework-orm once in the past few years.
- Add batch function for multiple sql statements.
- Add join support and in support.
- Add support for join and in.
- Add the ability to add where conditions (non-primary key dependent) to delete and update.
- Remove the need for a configuration file at startup.
- Replace package configuration in config file with annotation.
- Remove the restriction that IAlbianObject must be implemented.
- Remove the restriction that IAlbianObject must be implemented from IAlbian.
- 
- Remove dependency on albianj-framework, use spring boot or barebones.
- Remove the dependency on albianj-framework.
- Add encryption for sensitive information, and the ability to customize the key.
- Complete sample program.
- Complete documentation

# How to use it

Don't fork and use it, it's still under development.

# Contributors

# Documentation

1. [Reflections on previous architectures, including albianj](http://www.94geek.com/posts/2022/arch-confessions-preface/arch-confessions-preface-20220520.html "Architecture Confessions")



# The real reason

Crowning words are all spoken, so let's have a few rants!

Marked for December 12, 2023.

Why pick up Albianj-framework-orm again?

The current ORM looks like this:

! [Java's ORM framework](. /image/orm-tools-in-java2.png)

I've been writing a little something for fun for a few days now, and I've been trying out java's ORM frameworks, and there aren't too many to choose from. Generally myb and hbm two choose one, JPA use or have some less it! Most of the domestic Internet should be the world of myb, so use it.
Summarized, is the "MT mode" for rapid development, there is really no cool points. Even with some of the hotter plug-ins, still can not get my point. On the contrary, they are all features of the "set of the whole", complex tax a whole lot, not that kind of beauty of the right place.
Because I'm not an orthodox java programmer, the perspective of ORM is also a bit strange.
This is also from the other side of the illustration of a point: the program with the cool cool and it is really good to use is not particularly relevant. Mainly depends on the user's degree of habit. If you are used to it, even the most complex ones will be taken for granted, and vice versa.
