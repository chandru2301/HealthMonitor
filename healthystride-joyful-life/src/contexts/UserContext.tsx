import React, { createContext, useContext, useState, useEffect } from "react";

interface UserContextType {
  currentUserId: number | null;
  setCurrentUserId: (id: number | null) => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentUserId, setCurrentUserId] = useState<number | null>(() => {
    const stored = localStorage.getItem("currentUserId");
    return stored ? parseInt(stored, 10) : null;
  });

  useEffect(() => {
    if (currentUserId !== null) {
      localStorage.setItem("currentUserId", currentUserId.toString());
    } else {
      localStorage.removeItem("currentUserId");
    }
  }, [currentUserId]);

  return (
    <UserContext.Provider value={{ currentUserId, setCurrentUserId }}>
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("useUser must be used within UserProvider");
  }
  return context;
};
