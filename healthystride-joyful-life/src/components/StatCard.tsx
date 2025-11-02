import { LucideIcon } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { cn } from "@/lib/utils";

interface StatCardProps {
  title: string;
  value: string | number;
  icon: LucideIcon;
  subtitle?: string;
  trend?: string;
  variant?: "default" | "primary" | "accent" | "success";
}

export const StatCard = ({ 
  title, 
  value, 
  icon: Icon, 
  subtitle, 
  trend,
  variant = "default" 
}: StatCardProps) => {
  const gradientClasses = {
    default: "",
    primary: "bg-gradient-to-br from-primary/10 to-primary-glow/10 border-primary/20",
    accent: "bg-gradient-to-br from-accent/10 to-accent/5 border-accent/20",
    success: "bg-gradient-to-br from-success/10 to-success/5 border-success/20"
  };

  const iconClasses = {
    default: "text-muted-foreground",
    primary: "text-primary",
    accent: "text-accent",
    success: "text-success"
  };

  return (
    <Card className={cn("transition-all hover:shadow-lg", gradientClasses[variant])}>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">{title}</CardTitle>
        <Icon className={cn("h-5 w-5", iconClasses[variant])} />
      </CardHeader>
      <CardContent>
        <div className="text-3xl font-bold">{value}</div>
        {subtitle && (
          <p className="text-xs text-muted-foreground mt-1">{subtitle}</p>
        )}
        {trend && (
          <p className="text-xs text-success mt-1">{trend}</p>
        )}
      </CardContent>
    </Card>
  );
};
