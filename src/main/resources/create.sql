create table message (
    id uuid,
    sender uuid,
    receiver uuid,
    chat uuid,
    type text,
    data text,
    nonce text,
    created timestamp,
    primary key (id)
);
create index receiver_idx on message (receiver);
create index chat_idx on message (chat);

create table customer (
     id uuid,
     pk text,
     primary key (id)
);