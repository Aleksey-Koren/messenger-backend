create table message (
    id UUID,
    sender uuid,
    receiver uuid,
    chat uuid,
    type integer,
    data text,
    nonce text,
    created timestamp(3),
    primary key (id)
);
create index receiver_idx on message (receiver);
create index chat_idx on message (chat);

create table customer (
     id UUID,
     public_key text,
     primary key (id)
);