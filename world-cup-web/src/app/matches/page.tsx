import { MatchesDateExplorer } from "@/components/matches/matches-date-explorer";

export const metadata = {
  title: "Matches",
};

export default function MatchesPage() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-3xl font-bold text-white">Matches</h1>
      <p className="mt-2 text-emerald-100/70">
        Browse fixtures day by day throughout the tournament.
      </p>
      <div className="mt-8">
        <MatchesDateExplorer />
      </div>
    </div>
  );
}
