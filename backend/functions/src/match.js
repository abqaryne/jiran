const FACTORS = Object.freeze(['sleep','cleanliness','noise','cooking','smoking','pets','budget']);

function invalid(message) {
  const error = new TypeError(message);
  error.code = 'invalid-argument';
  throw error;
}
function object(value) {
  return value !== null && typeof value === 'object' && !Array.isArray(value);
}
function integer(value, max = Number.MAX_SAFE_INTEGER) {
  return Number.isSafeInteger(value) && value >= 0 && value <= max;
}
function ratio(numerator, denominator) {
  const n = BigInt(numerator), d = BigInt(denominator);
  return Number((n * 20000n + d) / (2n * d));
}
function validateProfile(profile) {
  if (!object(profile) || typeof profile.id !== 'string'
      || !/^[A-Za-z0-9_-]{1,128}$/.test(profile.id) || !object(profile.answers)) {
    invalid('Provide an ID and normalized questionnaire answers');
  }
  for (const factor of FACTORS) {
    const value = Object.hasOwn(profile.answers, factor) ? profile.answers[factor] : null;
    if (value == null) continue;
    let valid;
    if (factor === 'sleep') valid = integer(value, 1439);
    else if (['cleanliness','noise','cooking'].includes(factor)) valid = integer(value, 4);
    else if (factor === 'smoking' || factor === 'pets') valid = typeof value === 'boolean';
    else valid = object(value) && integer(value.minSen) && integer(value.maxSen) && value.minSen <= value.maxSen;
    if (!valid) invalid(`Invalid normalized ${factor} answer`);
  }
  const days = profile.classDays;
  if (days != null && (!Array.isArray(days) || days.length > 7
      || days.some(day => !Number.isInteger(day) || day < 1 || day > 7)
      || new Set(days).size !== days.length)) invalid('Invalid ISO class weekdays');
}
function similarity(factor, a, b) {
  if (a == null || b == null) return null;
  if (factor === 'sleep') {
    const delta = Math.abs(a - b);
    return ratio(720 - Math.min(delta, 1440 - delta), 720);
  }
  if (['cleanliness','noise','cooking'].includes(factor)) return ratio(4 - Math.abs(a - b), 4);
  if (factor === 'smoking' || factor === 'pets') return a === b ? 10000 : 0;
  const union = Math.max(a.maxSen, b.maxSen) - Math.min(a.minSen, b.minSen);
  const intersection = Math.max(0, Math.min(a.maxSen, b.maxSen) - Math.max(a.minSen, b.minSen));
  return union === 0 ? 10000 : ratio(intersection, union);
}

/** Inputs must already be authorized and mapped to the versioned normalized schema. */
function rankMatches(input) {
  if (!object(input)) invalid('Provide subject, candidates and explicit weight configuration');
  const {subject, candidates, config} = input;
  if (!object(config) || typeof config.version !== 'string' || !/^[A-Za-z0-9_.-]{1,100}$/.test(config.version)
      || !object(config.weights) || Object.keys(config.weights).length !== FACTORS.length
      || FACTORS.some(f => !Object.hasOwn(config.weights, f) || !integer(config.weights[f], 10000))
      || FACTORS.reduce((sum,f) => sum + config.weights[f], 0) !== 10000) {
    invalid('Provide versioned weights for all seven factors summing to 10000');
  }
  validateProfile(subject);
  if (!Array.isArray(candidates) || candidates.length > 100) invalid('At most 100 candidates are allowed');
  const ids = new Set();
  for (const candidate of candidates) {
    validateProfile(candidate);
    if (ids.has(candidate.id)) invalid('Duplicate candidate ID');
    ids.add(candidate.id);
  }
  const matches = candidates.filter(c => c.id !== subject.id).map(candidate => {
    const factors = FACTORS.map(factor => ({factor, weightBasisPoints:config.weights[factor],
      similarityBasisPoints:similarity(factor,
        Object.hasOwn(subject.answers, factor) ? subject.answers[factor] : null,
        Object.hasOwn(candidate.answers, factor) ? candidate.answers[factor] : null)}));
    const missingFactors = factors.filter(f => f.weightBasisPoints > 0 && f.similarityBasisPoints === null).map(f => f.factor);
    const sum = factors.reduce((total,f) => total + BigInt(f.weightBasisPoints) * BigInt(f.similarityBasisPoints ?? 0), 0n);
    const scoreBasisPoints = missingFactors.length ? null : Number((sum + 5000n) / 10000n);
    return {candidateId:candidate.id, status:missingFactors.length ? 'insufficient_data' : 'scored',
      scoreBasisPoints, score:scoreBasisPoints === null ? null : scoreBasisPoints / 100,
      missingFactors, factors,
      overlappingClassDays:subject.classDays == null || candidate.classDays == null ? null
        : subject.classDays.filter(day => candidate.classDays.includes(day)).sort((a,b) => a-b),
      reasons:factors.filter(f => f.weightBasisPoints > 0 && f.similarityBasisPoints !== null).map(f => ({
        factor:f.factor, code:f.similarityBasisPoints >= 7500 ? 'similar'
          : f.similarityBasisPoints <= 2500 ? 'different' : 'partial_overlap',
      })),
    };
  });
  matches.sort((a,b) => (b.scoreBasisPoints ?? -1) - (a.scoreBasisPoints ?? -1)
    || (a.candidateId < b.candidateId ? -1 : a.candidateId > b.candidateId ? 1 : 0));
  return {status:'ok',formulaVersion:'normalized-v1',weightVersion:config.version,matches};
}
module.exports = {rankMatches};
