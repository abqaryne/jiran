const {session} = require('../src/session');

describe('session authorization', () => {
  test('rejects a caller without authentication', () => {
    expect(() => session({data: {}})).toThrow('Authentication required');
  });

  test('rejects an authenticated user without verified email', () => {
    expect(() => session({auth: {uid: 'u1', token: {email_verified: false}}}))
      .toThrow('Verified email required');
  });

  test('does not accept a truthy string as verified email', () => {
    expect(() => session({auth: {uid: 'u1', token: {email_verified: 'true'}}}))
      .toThrow('Verified email required');
  });

  test('returns token identity even when payload impersonates another user', () => {
    expect(session({
      auth: {uid: 'u1', token: {email_verified: true}},
      data: {userId: 'u2'},
    })).toEqual({uid: 'u1', status: 'ok'});
  });
});
